---
layout: default
title: Memory table
parent: Outputs
grand_parent: API reference
nav_order: 27
---
# Memory table

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Overview](#overview)
  - [Sample type](#sample-type)
- [Querying](#querying)
- [Custom Table implementation](#custom-table-implementation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Overview

Memory tables allows you to store the values of the stream to query it later for different purposes. To create a memory table you just define as any other output specifying the name of the table:

```kotlin
stream.toTable("tableName")
```

Table name must be unique for your execution. If it's not, it'll fail when you try to execute the stream.

The amount of data you can keep in memory is limited but you can set to any valid value by specifying a certain parameter when creating a table output:

```kotlin
stream.toTable("tableName", 120.m)
```

The second parameter has type [TimeMeasure](../types/time-measure.md).

The table can work with any type you desire, on any stream, that type just needs to be a non-nullable.

### Sample type

For the type [Sample](../readme.md#sample) there is additional API available. The method is similar to the one menthioned above:

```kotlin
stream.toSampleTable("tableName", 120.m)
```

There is one additional parameter you may specify. For certain use cases like [audio streaming](../../http/readme.md#audio-service) it would be more efficient if the data is stored with larger granularity to reduce memory footprint and CPU usage. If you specify `sampleVectorBufferSize` greater than 0, samples will be stored as [SampleVector](../readme.md#samplevector) with specified length. Though that would mean you can read whole buffer all at once only, for audio streaming that means increased latency. So choose wisely. Even the buffer of 512 samples reduces CPU usage and increases streaming thoughput drastially, and the latency is less than 100ms for sample rate 44100Hz.

## Querying

While the stream is running the data is being stored into a table, where you can query it. The data is accessible regardless if the stream is over and closed or it is still being processed. However if you're trying to access data during execution it's not guaranteed to be presented as data availability depends on the overall stream performance and oyu need to write your code safely.

To query the data you need first locate the table by its name from table registry. You need explicitly specify the type the table keeps, for example `Sample`. The table has type `io.wavebeans.lib.io.table.TimeseriesTableDriver<T>`:

```kotlin
val table = TableRegistry.instance().byName<Sample>("tableName")
```

Once you've located the table you can start querying the table. Any query returns the result as `BeanStream<T>`, where the `T` is the type table works with. Once you get the stream you can work with as any other `BeanStream<T>` with no limitations.

Currently you can do the following with the table:

* `last(interval: TimeMeasure): BeanStream<T>` -- gets the last N time units out of the table, the parameter is [TimeMeasure](../types/time-measure.md). In provided example, it may return as exactly 2 seconds or less, even emptr stream, depending how much data is available.
* `timeRange(from: TimeMeasure, to: TimeMeasure): BeanStream<T>` -- gets the exact time range between `from` and `to` parameters which are both are [TimeMeasure](../types/time-measure.md) type. The same as method `last()`, it may return the stream with the length `to - from`, or less, or even empty. If `from > to`, it'll return the empty stream.
* `firstMarker(): TimeMeasure` -- return the time marker of the first value in the table. *Remember if the stream is still running the value keep changing*
* `lastMarker(): TimeMeasure` -- return the time marker of the last value in the table. *Remember if the stream is still running the value keep changing*

A few examples, assuming table is defined as above and has type `Sample`:

* return last 2 seconds

    ```kotlin
    table.last(2.s) 
        // we may store it to csv file
        .toCsv("file:///path/to/file.csv")
    ```

* get a range from 1.5s to 4.5s

    ```kotlin
    table.timeRange(1500.ms, 1500.ms + 3.s)
        // change an amplitude and sum up with different sample stream
        .map { it * 2 } + anotherSampleStream 
    ```

* wait for data be processed before querying

    ```kotlin
    // assuming data is being processed in a separate thread
    // wait for data to be accessible
    while (table.firstMarker() == null) { sleep(0) }
    // wait for enough data to be processed
    while (table.lastMarker() ?: 0.s < 3.s) { sleep(0) }
    // and get that piece
    table.timeRange(0.s, 3.s)
    ```
  
## Custom Table implementation

By default, the simple in memory table is used implemented by driver `io.wavebeans.lib.table.InMemoryTimeseriesTableDriver`. It is basically a wrapper arround double ended queue. It allows to stream data efficiently, however querying data is not very efficient. If you have a better idea for the table for your use case, you can always develop your own. To use that table driver implementation, you need to implement the `io.wavebeans.lib.table.TimeseriesTableDriver` and specify it while creating the TableOutput. For implementation details follow documentation for every method in the code.

```kotlin
// create implementation
class MyTimeseriesTableDriver: TimeseriesTableDriver<Int> {
    // implementation here
}

// create parameters object
val parameters = TableOutputParams<Int>(
    tableName = "myTable", 
    maximumDataLength = 120.s,
    tableDriverFactory = Fn.wrap { MyTimeseriesTableDriver() } 
)

// create output itself
val output = TableOutput<Int>(myStream, parameters)
```