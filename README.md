# Java implementation of WhatWg URL Living Standard

This project is a java implementation of the [WhatWg specification - Released on August 21th 2023](https://url.spec.whatwg.org/).
The main advantage of the WhatWg Url standard is that it fixes the various shortcomings and quirks of java.net.URL, RFC3986 and RFC3987, etc. 

The library is pretty slim (~50k), works with Java 8 and up, and only has a dependency on [ICU](https://unicode-org.github.io/icu/userguide/icu4j/)
For the more adventurous, the core implementation lives in the class UrlParser and is based on a state machine. 

For the record, I started this project years ago and used it in several projects. Always wanted to open-source it but never got the time to do it properly. This is long overdue and I hope you'll find it useful.
Note that I refactored the code recently and updated it to the latest WhatWg Url specification

The Java API closely follows [WhatWg API](https://url.spec.whatwg.org/#api).

Tests coverage is pretty good (3000+ tests). Some test data are borrowed from [Web-Platform](https://github.com/web-platform-tests/wpt/tree/master/url/resources/), the cross-browser test suite (Safari, Chrome, Firefox, Edge...))

You are obviously more than welcome to provide feedback, report issue and... provide pull requests ! :) 

# Build information
Gradle is the build system used by the project. A couple of commands:


`./gradlew assemble` to build the jar

`./gradlew build` to build and test the project

`./gradlew clean` to clean the build folder

`./gradlew spotlessApply` to format the code of the project

`./gradlew printVersion` to print the current version
