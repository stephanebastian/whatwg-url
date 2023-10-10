# Java implementation of WhatWg URL Living Standard

This project is a java implementation of the [WhatWg specification](https://url.spec.whatwg.org/) - The main advantage of the WhatWg Url standard is that it fixes the various shortcomings and quirks of java.net.URL, RFC3986 and RFC3987, etc.

It is in sync with [this specific commit (27 September 2023)](https://github.com/whatwg/url/commit/aa64bb27d427cef0d87f134980ac762cced1f5bb).

The library is pretty slim (~50k), works with Java 8 and up, and only has a dependency on [ICU](https://unicode-org.github.io/icu/userguide/icu4j/).
Tests coverage is pretty good (3000+ tests). Some test data are borrowed from [Web-Platform](https://github.com/web-platform-tests/wpt/tree/master/url/resources/), the cross-browser test suite (Safari, Chrome, Firefox, Edge...)). 
As a side note, there is a basic benchmark (built with jmh) that iterates over 500+ 'typical' urls and measures the throughput (350000 ops/s on an AMD Ryzen 5, but your mileage may vary)
For the more adventurous/curious, the interesting bits live in the class UrlParser and is based on a state machine.

For the record, I started this project years ago and used it in several projects. I always wanted to open-source it, but never got the time to do it properly. This is long overdue, and I hope you'll find it useful.

You are obviously more than welcome to provide feedback, report issue and... provide pull requests ! :)

# Java API

The Java API closely follows [WhatWg API](https://url.spec.whatwg.org/#api). 
Note that both create methods may throw a ValidationException. However, calling a setter with a bad value does *not* thrown an exception but report them in the method validationErrors()

```
public interface Url {
  static boolean canParse(String url);
  static boolean canParse(String url, String baseUrl);
  static Url create();
  static Url create(String input);
  static Url create(String input, String baseUrl);
  
  String hash();
  Url hash(String value);
  String host();
  Url host(String value);
  String hostname();
  Url hostname(String value);
  String href();
  Url href(String value);
  String origin();
  String password();
  Url password(String value);
  String pathname();
  Url pathname(String value);
  String port();
  Url port(String value);
  String protocol();
  Url protocol(String value);
  String search();
  Url search(String value);
  UrlSearchParams searchParams();
  String toJSON();
  String username();
  Url username(String value);
  // not in the spec, but very useful to list validation errors when parsing the initial raw url or when setting properties.
  Collection<ValidationError> validationErrors();
}

public interface UrlSearchParams {
  UrlSearchParams append(String name, String value);
  Collection<String> delete(String name);
  boolean delete(String name, String value);
  UrlSearchParams entries(BiConsumer<String, String> consumer);
  String get(String name);
  Collection<String> getAll(String name);
  boolean has(String name);
  boolean has(String name, String value);
  UrlSearchParams set(String name, String value);
  int size();
  UrlSearchParams sort();
}

public enum ValidationError {
  ... various enum values

  public String description();
  public boolean isFailure();
}

```

# Usage
Typical code to parse a url

```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void parseUrl() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    System.out.println(url.hash());         // #hash1
    System.out.println(url.host());         // www.myurl.com 
    System.out.println(url.hostname());     // www.myurl.com
    System.out.println(url.href());         // http://www.myurl.com/path1?a=1&b=2#hash1
    System.out.println(url.origin());       // http://www.myurl.com
    System.out.println(url.password());     // 
    System.out.println(url.pathname());     // /path1
    System.out.println(url.port());         //
    System.out.println(url.protocol());     // http
    System.out.println(url.search());       // ?a=1&b=2
    System.out.println(url.searchParams()); // a=1&b=2
    System.out.println(url.username());     // 
}
```

Typical code to parse a relative url

```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void parseRelativeUrl() {
    Url url = Url.create("path1?a=1&b=2#hash1", "http://www.myurl.com/path2?c=3&d=2#hash2");
    System.out.println(url.hash());         // #hash1
    System.out.println(url.host());         // www.myurl.com 
    System.out.println(url.hostname());     // www.myurl.com
    System.out.println(url.href());         // http://www.myurl.com/path1?a=1&b=2#hash1
    System.out.println(url.origin());       // http://www.myurl.com
    System.out.println(url.password());     // 
    System.out.println(url.pathname());     // /path1
    System.out.println(url.port());         //
    System.out.println(url.protocol());     // http
    System.out.println(url.search());       // ?a=1&b=2
    System.out.println(url.searchParams()); // a=1&b=2
    System.out.println(url.username());     // 
}
```

Set properties
```
import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;

public void setProperties() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    // hash
    System.out.println(url.hash());         // #hash1
    url.hash("hash2");
    System.out.println(url.hash());         // #hash2
    // host
    System.out.println(url.host());         // www.myurl.com
    url.host("anotherhost.io");
    System.out.println(url.host());         // anotherhost.io
    // set other properties such as username, password, pathname, port, protocol, etc.
}

```

# Build information
Gradle is the build system used by the project. A couple of useful commands:

`./gradlew assemble` to build the jar

`./gradlew build` to build and test the project

`./gradlew clean` to clean the build folder

`./gradlew spotlessApply` to format the code of the project

`./gradlew printVersion` to print the current version

`./gradlew jmh` to run the benchmark

`./gradlew publishToSonatype closeSonatypeStagingRepository` to publish a new artifact to nexus staging servers

`./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository` to release to maven central
