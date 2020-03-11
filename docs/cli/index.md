---
layout: default
title: Command Line Interface
nav_order: 5
---
# Command Line Interface

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation instructions](#installation-instructions)
- [Usage](#usage)
  - [Basic example](#basic-example)
  - [Parameters explained](#parameters-explained)
  - [Writing scripts](#writing-scripts)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Overview

WaveBeans CLI is the tool to automate various tasks related to audio signal processing. What it does: it allows you to write the program on Kotlin Script and run it via console in different modes: as local tool, on local distributed environment, or (potentially) on remote cluster. Depending on your task you may benefit or not from running task Locally in Distributed mode, where the execution is launched within defined number of threads, by default, the program is evaluated in a single thread.

## Requirements

The tool requires to have JRE 8+ installed and be configured properly. Please follow installation instructions, [OpenJDK](https://openjdk.java.net/install/)

Kotlin installed, kotlinc which can be located either via KOTLIN_HOME or PATH. Follow instructions on [official page](https://kotlinlang.org/docs/tutorials/command-line.html)

## Installation instructions

* Choose the appropriate version
* Download the binaries from `https://dl.bintray.com/wavebeans/wavebeans/wavebeans-$VERSION.zip`

```bash
 curl -LO https://dl.bintray.com/wavebeans/wavebeans/wavebeans-$VERSION.zip
```

* Unpack them

```bash
unzip wavebeans.zip
```

* To avoid providing a full path every time make an alias to WaveBeans CLI

```bash
echo "alias wavebeans=$(pwd)/wavebeans/bin/wavebeans" >> ~/.zshrc
```

* or add it to PATH variable to be located automatically:

```bash
echo "PATH=\$PATH:$(pwd)/wavebeans/bin/" >> ~/.zshrc
```

* Restart the shell and try running the tool, you should see the help output:

```bash
wavebeans
```

## Usage

### Basic example 

Let's try to write a small program and launch it. The program as simple as generate 10 seconds of sinusoid and store it into the `.wav` file.
 
```kotlin
440.sine().trim(1000).toMono16bitWav("file:///path/to/the/file") // define what you want to do 
    .out() // register output stream!
```

To run this program you have two options:

1. Pass it as inline parameter:

```bash
export FILE="$(pwd)/sine440.wav" && wavebeans --execute "440.sine().trim(1000).toMono16bitWav(\"file://$FILE\").out()"
```

2. Or store the script into the file and execute the file

```bash
export FILE="$(pwd)/sine440.wav" && echo "440.sine().trim(1000).toMono16bitWav(\"file://$FILE\").out()" > script.kts 
wavebeans --execute-file script.kts
```

Either way you'll find `sine440.wav`created in the same directory you're in.

### Parameters explained

You can always find the quick help in the tool itself by providing `-h` or `--help` as a parameters, but here are some quick explanation.

**Execute**

As was stated above, you may execute inline script (`-e` or `--execute`) or script from the file (`-f` or `--execute-file`) which basically has no functional difference. The API is fully covered in the LIB. **The most important thing which makes LIB functionality and CLI functionality different is that for the output to take effect you must add `.out()` to register your output, otherwise just nothing gonna happen. You need to call `.out()` for evry single output stream you define.**

**Run mode**

You may choose in what mode to run (`-m` or `--run-mode`). By default you'll run it just in single thread mode (`local`), but you may chooose to run in multi-threaded environment (`local-distributed`).

For `local-distributed` mode you need to pass additional parameters:
* how many partitioned your processing topology will be tried to split up to: `-p` or `--partitions`. You may benefit from using it, but that is not guaranteed. Each partition is processed separately, the stream is broken down by chunks of a few hundreds samples, which are processed in parallel. Not all of the operations support partitioning, so you may end up just using one partition even if you specify more.
* to be able to benefit from parallel processing you need to specify number of threads to use: `-t` or `--threads`. Each thread will be heavily used, so there is no point of specifying more threads than you have cores available. Also, you need to make sure your processing topology can be split up into specified number if threads, otherwise threads gonna be underutilized.

**More information about execution**

[TODO cover `--time` and `-v`]

### Writing scripts

Script is based on the functionality of the lib and everything provided there can be used. So follow LIB documentation to get idea how to solve one or another problem, it's no different that writing your own JVM application.

**Imports**

WaveBeans CLI tool executes the script written on Kotlin language. It has full support not only for library function but any other Kotlin SDK functionality. By default, WaveBeans and [Kotlin standard](https://kotlinlang.org/docs/reference/packages.html#default-imports) packages are imported, but you can import the one you need, just specify them on top of you script as usual:

```kotlin
import java.io.File

val file = File.createTempFile("test", ".csv")
440.sine().trim(Long.MAX_VALUE).toCsv("file://${file.absolutePath}").out()
```

You may specify as many imports as you want, while you're following [imports grammar](https://kotlinlang.org/docs/reference/packages.html#imports)

**Outputs**

WaveBeans processing is declarative. That means you define the processing logic and it is evaluated when terminal action is called. For WaveBeans the terminal action is output. Though it should be connected to the execution environment, in order to be processed.

For the script in order to track the output you should call explicitly `.out()`:

```kotlin

440.sine().trim(1000).toCsv("file:///path/to/file.csv") // specify your handling logic
    .out() // register output to be processed 

```

Otherwise, if you don't call it that lines of code won't be evaluated. Also, if you register output more than once by calling `.out()` a few times, it will be registered as two different output and be evaluated twice.

### HTTP API

WaveBeans has built HTTP API to run variety of queries while the stream is being evaluated. More about exact functionality read in [HTTP API reference](../http/readme.md). CLI supports running the HTTP API along with your script. 

To start the server specify the port you want to run it on via `--http` flag. The range of ports from 1 to 65536, though on Unix-like system to run on ports less than 1024 administrator privileges are required, and overall is not recommended to avoid interfering with standard services.

When the script is stop running the HTTP server is also being shutdown, however you may want to leave it running. To achieve that you need to specify `--http-wait` flag and specify the number of seconds to keep the server running after execution is completed, or even do not stop at all by specifying -1.

#### Example with Table API

HTTP Service may provide different APIs, in this example we'll take a look at using it via calling Table API. Tables allows to store values and query it later. More about it you can read in [Table Output reference](../lib/outputs/table-output.md) and [Table Service API](../http/readme.md#table-service).

First of all let's create the `script.kts` with the following content:

```kotlin
440.sine().toTable("sine440", 1.m).out()
```

That script generates sinusoid of 440 Hz and stores it into the table name `sine440`. The table keeps only last 1 minute of all samples.

Then we run that script with CLI command tool and leave it working in background or in different console. HTTP we'll start on port 12345 and tell the HTTP server wait forever, however it is not really required here as provided script won't finish either.

```bash
wavebeans --execute-file script.kts --http 12345 --http-wait -1
```

While the script is running let's run a couple of queries via calling Table API over HTTP using `curl` command:

```bash
curl http://localhost:12345/table/sine440/last/1.ms/
```

Which return something like this:

```json
{"offset":368162653061,"value":0.6921708580045118}
{"offset":368162675736,"value":0.6455957967580076}
{"offset":368162698411,"value":0.5964844019471909}
{"offset":368162721086,"value":0.5450296159930959}
{"offset":368162743761,"value":0.4914335880185192}
{"offset":368162766436,"value":0.43590687909747267}
{"offset":368162789111,"value":0.3786676351923965}
{"offset":368162811786,"value":0.3199407308930018}
{"offset":368162834461,"value":0.2599568846828487}
```