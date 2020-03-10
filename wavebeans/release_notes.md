---
layout: page
nav_order: 2
parent: WaveBeans
---

Release notes
=========

Version 0.0.2 on 2020-03-10
------

* CLI: Using kotlinc to compile script.
* Documentation: restructured to publish on [wavebeans.io](https://wavebeans.io)
* HTTP API: introduced [HTTP interface](/docs/http/readme.html) for accessing internal resources
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