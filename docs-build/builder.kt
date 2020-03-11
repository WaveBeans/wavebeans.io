import java.io.*
import java.io.File.*
import java.nio.file.*

data class FileIndex(
    val file: File,
    val header: String,
    val relPath: String,
    val isSupportFile: Boolean
)

val index = mutableMapOf<String, FileIndex>()
var debug = false

fun main(args: Array<String>) {

    fun Array<String>.argument(key: String, optional: Boolean = false): String? =
        this.firstOrNull {it.startsWith("--$key=")}
            ?.let {it.removePrefix("--$key=")}
            ?: if (optional) null else throw IllegalArgumentException("--$key should be specfied")

    val baseDirectory = File(args.argument("base")!!)
    val outputDirectory = args.argument("output")!!
    val navOrder = args.argument("nav-order", optional = true)?.toInt()
    val validSupportFileExtensions = args.argument("support-extensions", optional = true)?.split(",")?.toSet()

    if (args.any {it == "--debug"})
        debug = true

    println("""
        Running with parameters:
            baseDirectory=$baseDirectory
            outputDirectory=$outputDirectory
        ============
    """.trimIndent())

    index(baseDirectory, baseDirectory, validSupportFileExtensions)
    
    File(outputDirectory).delete()

    index.entries.forEach { e ->
        val file = e.value
        val docFile = File(outputDirectory + file.relPath)
        docFile.getParentFile().mkdirs()
        if (file.isSupportFile) {
            println("""
            Support file $file
            ============
            """.trimIndent())
            val content = file.file.readBytes()
            docFile.writeBytes(content)
        } else {
            val filePath = File(e.key).toPath()
            val parent = parentOf(file.file, filePath)
            val grandParent = if (parent != null) parentOf(parent.file, File(parent.relPath).toPath()) else null

            val hasChildren = index.entries
                .filter { it.value.file.name.endsWith(".md") }
                .map { parentOf(it.value.file, File(it.key).toPath()) }
                .filter { it?.file == file.file }
                .any()

            println("""
                for $file 
                    parent is $parent
                    grandParent is $grandParent
                    hasChildren is $hasChildren
                ============
            """.trimIndent())

            docFile.getParentFile().mkdirs()
            docFile.createNewFile()
            val content = mutableListOf<String>()
            content += "---"
            content += "layout: default"
            content += "title: ${file.header}"
            if (parent != null) content += "parent: ${parent.header}"
            if (grandParent != null) content += "grand_parent: ${grandParent.header}"
            if (hasChildren) content += "has_children: true"
            if (parent == null && grandParent == null) content += "nav_order: $navOrder"
            content += "---"
            content += file.file.readLines()

            docFile.writeText(content.joinToString("\n"))
            if (docFile.name.toLowerCase() == "readme.md") {
                docFile.renameTo(File(docFile.absolutePath.replace("readme.md", "index.md", ignoreCase = true)))
            }
        }
    }
}

fun parentOf(file: File, path: Path): FileIndex? {
    if (debug) println("Searching for parent for file=$file on path=$path")
    val lastNameIdx = path.nameCount - if (path.endsWith("readme.md")) 2 else 1
    val parentPath = if (lastNameIdx > 0) path.subpath(0, lastNameIdx) else null
    val parentIndex = (parentPath?.toString()?.let {it + "/"} ?: "") + "readme.md"
    val parent = index[parentIndex]?.let{ if (it.file == file) null else it }
    if (debug) println("The parent is $parent")
    return parent
}

fun index(baseDirectory: File, file: File, validSupportFileExtensions: Set<String>?) {
    if (file.isDirectory()) {
        file.listFiles().forEach { index(baseDirectory, it, validSupportFileExtensions) }
    } else {
        val relPath = baseDirectory.toPath().relativize(file.toPath()).toString()
        if (file.name.endsWith(".md")) {
            val header = (file.readLines().firstOrNull { it.trim().isNotEmpty() || it.trim().startsWith("# ") } ?: "NONE")
                .removePrefix("# ")
            index[relPath.toLowerCase()] = FileIndex(file, header, relPath, false)
        } else if (validSupportFileExtensions == null || validSupportFileExtensions.any {file.name.endsWith(".$it")}) {
            index[relPath.toLowerCase()] = FileIndex(file, "", relPath, true)
        }
    }
}