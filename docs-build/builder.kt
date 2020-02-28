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

fun main(args: Array<String>) {
    val baseDirectory = File(
        if (args.isEmpty())
            "/Users/asubb/work/wavebeans.io/docs-build/wavebeans/docs/user/lib"
        else
            args[0]
    )
    val outputDirectory = if (args.isEmpty()) 
        "/Users/asubb/work/wavebeans.io/docs/api/"
    else
        args[1]

    println("""
    Running with parameters:
        baseDirectory=$baseDirectory
        outputDirectory=$outputDirectory
        ============
    """.trimIndent())

    index(baseDirectory, baseDirectory)
    
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
            val grandParent = if (filePath.nameCount > 1) parentOf(file.file, filePath.subpath(0, filePath.nameCount - 1)) else null

            val hasChildren = index.entries
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
            content += "---"
            content += file.file.readLines()

            docFile.writeText(content.joinToString("\n"))
        }
    }
}

fun parentOf(file: File, path: Path): FileIndex? {
        val lastNameIdx = path.nameCount - if (path.endsWith("readme.md")) 2 else 1
        val parentPath = if (lastNameIdx > 0) path.subpath(0, lastNameIdx) else null
        val parentIndex = (parentPath?.toString()?.let {it + "/"} ?: "") + "readme.md"
        val parent = index[parentIndex]?.let{ if (it.file == file) null else it }
        return parent
}

fun index(baseDirectory: File, file: File) {
    if (file.isDirectory()) {
        file.listFiles().forEach { index(baseDirectory, it) }
    } else {
        val relPath = baseDirectory.toPath().relativize(file.toPath()).toString()
        if (file.name.endsWith(".md")) {
            val header = file.readLines().firstOrNull { it.trim().isNotEmpty() } ?: "NONE"
            index[relPath.toLowerCase()] = FileIndex(file, header, relPath, false)
        } else {
            index[relPath.toLowerCase()] = FileIndex(file, "", relPath, true)
        }
    }
}