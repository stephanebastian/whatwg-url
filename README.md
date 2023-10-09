# Java implementation of WhatWg URL Living Standard

This project is a java implementation of the [WhatWg specification - 27 September 2023](https://url.spec.whatwg.org/).
The main advantage of the WhatWg Url standard is that it fixes the various shortcomings and quirks of java.net.URL, RFC3986 and RFC3987, etc. 

The library is pretty slim (~50k), works with Java 8 and up, and only has a dependency on [ICU](https://unicode-org.github.io/icu/userguide/icu4j/)
For the more adventurous, the core implementation lives in the class UrlParser and is based on a state machine. 

For the record, I started this project years ago and used it in several projects. Always wanted to open-source it but never got the time to do it properly. This is long overdue and I hope you'll find it useful.
Note that I refactored the code recently and updated it to the latest WhatWg Url specification

The Java API closely follows [WhatWg API](https://url.spec.whatwg.org/#api). It provide an extra method Url.validationErrors() to list potential validations that may have been reported when parsing the raw url or when setting properties

Tests coverage is pretty good (3000+ tests). Some test data are borrowed from [Web-Platform](https://github.com/web-platform-tests/wpt/tree/master/url/resources/), the cross-browser test suite (Safari, Chrome, Firefox, Edge...))

You are obviously more than welcome to provide feedback, report issue and... provide pull requests ! :) 

# Usage
Typical code to parse a url

```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void parseUrl() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.myurl.com");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path1");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?a=1&b=2");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
}
```

Typical code to parse a relative url

```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void parseRelativeUrl() {
    Url url = Url.create("path1?a=1&b=2#hash1", "http://www.myurl.com/path2?c=3&d=2#hash2");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.myurl.com");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path1");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?a=1&b=2");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
}
```

Set properties
```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void setProperties() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    url.hash("hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    url.host("anotherhost.io");
    Assertions.assertThat(url.host()).isEqualTo("anotherhost.io");
    // set other properties such as username, password, pathname, port,protocol, etc.
}

```

# Build information
Gradle is the build system used by the project. A couple of commands:


`./gradlew assemble` to build the jar

`./gradlew build` to build and test the project

`./gradlew clean` to clean the build folder

`./gradlew spotlessApply` to format the code of the project

`./gradlew printVersion` to print the current version

`./gradlew jmh` to run the benchmark

`./gradlew publishToSonatype closeSonatypeStagingRepository` to publish a new artifact to nexus staging servers

`./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository` to release to maven central
