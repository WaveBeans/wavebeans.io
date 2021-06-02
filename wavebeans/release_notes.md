---
layout: page
nav_order: 2
parent: WaveBeans
---

Release notes
=========

Version 0.3.1 on 2021-06-01
------

* **[Breaking changes]** Maven repository migrated from BinTray to Maven Central. Updated the guides and related documentation. Also, the `filesystems` and `metrics` projects naming has changed to fit under the maven group ID `io.wavebeans`


Version 0.3.0 on 2021-01-02
------

* **[Breaking changes]** SampleArray renamed to [SampleVector](/docs/api/index.html#samplevector) as well as it has wider API and usage overall.
* **[Breaking changes]** Now based on Kotlin 1.4. Previous Kotlin versions are not guaranteed to work.
    * Kotlin 1.4.21
* [ [#31](https://github.com/WaveBeans/wavebeans/issues/31) ] [Flatten](/docs/api/operations/flatten.html) operation.
* [ [#86](https://github.com/WaveBeans/wavebeans/issues/86) ] [Managing](/docs/api/index.html#managed-type) and controlling [WAV](/docs/api/outputs/wav-output.html#controlling-output) and [CSV](/docs/api/outputs/csv-outputs.html#controlling-output) outputs
* [Output as a function](/docs/api/outputs/output-as-a-function.html)
* [Inverse FFT](/docs/api/operations/fft-operation.html#inverse-fft) implementation
* [Window functions](/docs/api/operations/fft-operation.html#window-functions) are available as separate convenience functions 
* [Resample](/docs/api/operations/resample-operation.html) operation
    * An automatic resampling for [wav-file inputs](/docs/api/inputs/wav-file.html#resampling)
* Monitoring: metrics system, internal collectors and Prometheus exporter. More in [docs](/docs/ops/monitoring.html). 
* Improvements on [SampleVector](/docs/api/index.html#samplevector) type, i.e. arithmetic operation with scalars, an operation on two non-nullable vector gets non-nullable result. 


Version 0.2.0 on 2020-08-12
------

* [ [#82](https://github.com/WaveBeans/wavebeans/issues/82) ] [Concatenation](/docs/api/operations/concatenation-operation.html) operation
* File systems support:
    * File System abstraction for a better support of different location types of operations on files.
    * DropBox implementation as File System, follow [usage guide](/docs/api/file-systems.html#dropbox-file-system).
* HTTP Service improvements: 
    * [ [#62](https://github.com/WaveBeans/wavebeans/issues/62) ] HTTP API improvements. Audio and Table service no longer require some parameters (sampleRate, sourceType) which can be inferred from the table itself.
    * HTTP service now may stream data out of the table with the help of [Audio Service](/docs/http/index.html#audio-service).
    * HTTP Service is CORS-enabled
* Table Output improvements:
    * If the table is based on finite stream the audio streaming to support the end of the stream as well.
    * Specific [Table API for Samples and SampleArrays](/docs/api/outputs/table-output.html#sample-type)
    * Table implementation can now be [provided as a parameter](/docs/api/outputs/table-output.html#custom-table-implementation).
    * Introduced remote table driver implementation and leveraging it in HTTP service, so now HTTP service may provide access to tables while running in distributed mode. More details in [documentation](/docs/http/index.html#distributed-mode)
    * Better contextual documentation for Table Output.
* Other: 
    * Introduced [SampleArray](/docs/api/index.html#samplearray) type for performance optimization of certain use cases.
    * [ [#59](https://github.com/WaveBeans/wavebeans/issues/59) ] Switched internal communication to gRPC.
    * [Bugfix] 24bit wave file storing and fetching hasn't been working properly.
    * [Bugfix] The table output is not correctly measuring time markers for complex objects 
    * [Internal] Wav Writer slighly refactored to be more reusable in different parts of the system.

Version 0.1.0 on 2020-05-18
------

Entering the **new era** with actual distributed processing. Now it's still Work In Progress though have some abilities to play around with.

* The first version of evaluation in Distributed mode. Follow the [docs](/docs/exe/index.html#distributed-mode).
* Proper multi-threaded mode that doesn't require serialization in place. Follow the [docs](/docs/exe/index.html#multi-threaded-mode).
* [ [#52](https://github.com/WaveBeans/wavebeans/issues/52) ] Custom class that requires measurement needs to implement `Measured` interface. See [updated section of documentation](/docs/api/operations/projection-operation.html#working-with-different-types)
* A first version of [Developer zone](/devzone/) -- a bunch of documents explaining ideas and architecture behind WaveBeans.

Version 0.0.3 on 2020-04-03
------

* Execution: **Breaking changes** `Overseer.eval()` now returns different type of result. Follow [execution documentation](/docs/exe/index.html).
* CLI: Using Kotlin Scripting as main engine. No need to install Kotlin SDK anymore
* Support of different [window functions](https://en.wikipedia.org/wiki/Window_function):
  * [Documentation](/docs/api/operations/map-window-function.html)
  * Implementation for [Sample](/docs/api/operations/map-window-function.html#stream-of-sample-type) type:
    * [rectangular](https://en.wikipedia.org/wiki/Window_function#Rectangular_window)
    * [triangular](https://en.wikipedia.org/wiki/Window_function#Triangular_window)
    * [blackman](https://en.wikipedia.org/wiki/Window_function#Blackman_window)
    * [hamming](https://en.wikipedia.org/wiki/Window_function#Hann_and_Hamming_windows)
  * Generic implementation for [any type](/docs/api/operations/map-window-function.html#stream-of-any-type)
* Better support for types using through HTTP/Table service, distributed execution and measuring for different purposes:
    * `FftSample`
    * `Window<T>`
    * `List<Double>`
* Detecting valid classloader depending on the environment you're running in (i.e. regular app, Jupyter, scripting, etc)
* Kotlin version upgrade to 1.3.70
* WaveBeans API is no longer exposing Kotlin Experimental API.

Version 0.0.2 on 2020-03-10
------

* CLI: Using kotlinc to compile script.
* Documentation: restructured to publish on [wavebeans.io](https://wavebeans.io)
* HTTP API: introduced [HTTP interface](/docs/http/index.html) for accessing internal resources
* Table: added [querying](/docs/api/outputs/table-output.html#querying) over HTTP API
* Inputs: using [List as input](/docs/api/inputs/list-as-input.html)
* Operations: merge operation can [merge streams of different types](/docs/api/operations/merge-operation.html#using-with-two-different-input-types)
* Output: writing samples to in-memory [table](/docs/api/outputs/table-output.html) for later [querying](/docs/api/outputs/table-output.html#querying)

Version 0.0.1 on 01/31/2020
------

This is the very first release of WaveBeans. It is considered to be Alpha version -- free to use but without any guarantees regarding quality and if API remains the same.

What's being released:

* Inputs:
  * [mono wav-files](/docs/api/inputs/wav-file.html)
  * [sine and sweep sines](/docs/api/inputs/sines.html)
  * [custom functions](/docs/api/inputs/function-as-input.html)
* Operations:
  * [basic arithmetic operations on streams](/docs/api/operations/arithmetic-operations.html)
  * [change amplitude of Sample stream](/docs/api/operations/change-amplitude-operation.html)
  * [FFT analysis](/docs/api/operations/fft-operation.html)
  * [Transformations with map function](/docs/api/operations/map-operation.html)
  * [Mixing stream using merge function](/docs/api/operations/merge-operation.html)
  * [Projections on streams](/docs/api/operations/projection-operation.html)
  * [Trimming infinite streams](/docs/api/operations/trim-operation.html)
  * [Windowing over the stream](/docs/api/operations/window-operation.html)
* Outputs
  * [mono wav-file](/docs/api/outputs/wav-output.html)
  * [CSV](/docs/api/outputs/csv-outputs.html)
  * [/dev/null](/docs/api/outputs/dev-null-output.html)