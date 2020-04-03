---
layout: page
nav_order: 1
parent: WaveBeans
---

Getting started
==========

It's important to understand how you're about to use the WaveBeans, however if you're new to the tool the easiest way would be to use [command line tool](/docs/cli/index.html) which provides comprehensive interface to the functionality and allows start working as soon as possible without deep dive.

Prerequisites
--------

Overall all you need is to have JRE/JDK 8+ installed and configured properly, so JAVA_HOME variable points to correct Java home folder.

Developing an audio application
WaveBeans is written on Kotlin, but it is compatible with all other JVM languages -- Java, Scala, etc.

If you want to use WaveBeans in your application just add it as a maven dependency. Here is what you would need to add into your `build.gradle` file:

Register the new maven repository the WaveBeans is hosted in:

```groovy
repositories {
    maven {
        name = "Bintray WaveBeans"
        url = uri("https://dl.bintray.com/wavebeans/wavebeans")
    }
}
```

Register WaveBeans `exe` and `lib` main libraries, you may not need `exe` if you won't be using execution capabilities, please find most recent version in [Release Notes](release_notes.html):

```groovy
dependencies {
    implementation "io.wavebeans:exe:$WAVEBEANS_VERSION"
    implementation "io.wavebeans:lib:$WAVEBEANS_VERSION"
}
```

Optionally you may be required to add regular kotlin runtime dependency if you don't have it:

```groovy
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
}
```

And start using it. Just create kotlin-file like this:

```kotlin
import io.wavebeans.execution.*
import io.wavebeans.lib.io.*
import io.wavebeans.lib.stream.*
import java.io.File

fun main() {
    // describe what you want compute
    val out = 440.sine()
            .trim(1000)
            .toMono16bitWav("file://" + File("sine440.wav").absoluteFile)

    // this code launches it in single threaded mode,
    // follow execution documentation for details
    LocalOverseer(listOf(out)).use { overseer ->
        if (!overseer.eval(44100.0f).all { it.get() }) {
            println("Execution failed. Check logs")
        }
    }
}
```

For more API capabilities follow [documentation](/docs/api/index.html)

Logging
--------

WaveBeans uses [slf4j](http://www.slf4j.org/) for logging, but it doesn't provide the default logging engine when it is being used inside application. You would need to configure it properly on your own.
