package io.github.stephanebastian.whatwg.url.impl;

import io.github.stephanebastian.whatwg.url.Url;
import io.github.stephanebastian.whatwg.url.ValidationError;
import io.github.stephanebastian.whatwg.url.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUrlApi {
  @Test
  public void createEmptyUrl() {
    Url url = Url.create();
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEmpty();
    Assertions.assertThat(url.host()).isEmpty();
    Assertions.assertThat(url.hostname()).isEmpty();
    Assertions.assertThat(url.href()).isEqualTo(":");
    Assertions.assertThat(url.origin()).isEqualTo("null");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEmpty();
    Assertions.assertThat(url.protocol()).isEqualTo(":");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.search()).isEmpty();
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(0);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
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

  @Test
  public void parseUrlThrowingException() {
    Assertions.assertThatThrownBy(() -> Url.create("http://xn--/"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.DOMAIN_TO_ASCII.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://exa%23mple.org"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.DOMAIN_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("foo://exa[mple.org"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.HOST_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://1.2.3.4.5/"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_TOO_MANY_PARTS.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://test.42"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_NON_NUMERIC_PART.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://255.255.4000.1"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_OUT_OF_RANGE_PART.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[::1"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_UNCLOSED.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[:1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_INVALID_COMPRESSION.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1:2:3:4:5:6:7:8:9]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_TOO_MANY_PIECES.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1::1::1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_MULTIPLE_COMPRESSION.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1:2:3!:4]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1:2:3:]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1:2:3]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV6_TOO_FEW_PIECES.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[1:1:1:1:1:1:1:127.0.0.1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_TOO_MANY_PIECES.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::.0.0.1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.0.xyz.1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.0xyz]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.00.0.1]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.0.0.1.2]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.0.0.4000]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_OUT_OF_RANGE_PART.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://[ffff::127.0.0]"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.IPV4_IN_IPV6_TOO_FEW_PARTS.description());
    Assertions.assertThatThrownBy(() -> Url.create("\\uD83D\\uDCA9"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.MISSING_SCHEME_NON_RELATIVE_URL.description());
    Assertions.assertThatThrownBy(() -> Url.create("\\uD83D\\uDCA9\\", "mailto:user@example.org"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.MISSING_SCHEME_NON_RELATIVE_URL.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://#fragment"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.HOST_MISSING.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://:443"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.HOST_MISSING.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://example.org:70000"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.PORT_OUT_OF_RANGE.description());
    Assertions.assertThatThrownBy(() -> Url.create("https://example.org:7z"))
        .isInstanceOf(ValidationException.class)
        .hasMessage(ValidationError.PORT_INVALID.description());
    // Assertions.assertThatThrownBy(() -> Url.create("myprotocol://www.myu
    // rl.com/path1?a=1&b=2#hash1")).isInstanceOf(ValidationException.class).hasMessage(ValidationError._INVALID_SCHEME.description());
    // Assertions.assertThatThrownBy(() -> Url.create("myprotocol://www.myu
    // rl.com/path1?a=1&b=2#hash1")).isInstanceOf(ValidationException.class).hasMessage(ValidationError._IPV4_NUMBER_PARSER.description());
    // Assertions.assertThatThrownBy(() -> Url.create("myprotocol://www.myu
    // rl.com/path1?a=1&b=2#hash1")).isInstanceOf(ValidationException.class).hasMessage(ValidationError._SEARCH_PARAMS_INIT.description());
  }

  @Test
  public void parseUrlWithValidationError() {
    Url url = Url.create("https://127.0.0.1./");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.IPV4_EMPTY_PART);
    url = Url.create("https://127.0.0x0.1");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.IPV4_NON_DECIMAL_PART);
    url = Url.create("https://example.org/>");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create(" https://example.org ");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("ht\ntps://example.org");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https://example.org/%s");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https:example.org");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.SPECIAL_SCHEME_MISSING_FOLLOWING_SOLIDUS);
    url = Url.create("https://example.org\\\\path\\\\to\\\\file\\");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    url = Url.create("https://user@example.org");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_CREDENTIALS);
    url = Url.create("ssh://user@example.org");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.INVALID_CREDENTIALS);
    // url = Url.create("//c:/path/to/file", "file:///c:/");
    // Assertions.assertThat(url.validationErrors().iterator().next()).isEqualTo(ValidationError.FILE_INVALID_WINDOWS_DRIVE_LETTER);
    url = Url.create("file://c:");
    Assertions.assertThat(url.validationErrors().iterator().next())
        .isEqualTo(ValidationError.FILE_INVALID_WINDOWS_DRIVE_LETTER_HOST);
  }

  @Test
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

  @Test
  public void setValidHash() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    url.hash("hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
  }

  @Test
  public void setValidHost() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    url.host("anotherhost.io");
    Assertions.assertThat(url.host()).isEqualTo("anotherhost.io");
  }

  @Test
  public void setValidHostname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.hostname("anotherhost.io");
    Assertions.assertThat(url.hostname()).isEqualTo("anotherhost.io");
  }

  @Test
  public void setValidHref() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.href("http://www.anotherurl.io/path2?c=3&d=4#hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
    Assertions.assertThat(url.host()).isEqualTo("www.anotherurl.io");
    Assertions.assertThat(url.hostname()).isEqualTo("www.anotherurl.io");
    Assertions.assertThat(url.href()).isEqualTo("http://www.anotherurl.io/path2?c=3&d=4#hash2");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.anotherurl.io");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path2");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?c=3&d=4");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
  public void setValidPassword() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.password("password2");
    Assertions.assertThat(url.password()).isEqualTo("password2");
  }

  @Test
  public void setValidPathname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.pathname("path2/path3");
    Assertions.assertThat(url.pathname()).isEqualTo("/path2/path3");
    url.pathname("/path4");
    Assertions.assertThat(url.pathname()).isEqualTo("/path4");
  }

  @Test
  public void setValidPort() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.port("443");
    Assertions.assertThat(url.port()).isEqualTo("443");
    url.port("80");
    Assertions.assertThat(url.port()).isEqualTo("");
  }

  @Test
  public void setValidProtocol() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.protocol("ftp");
    Assertions.assertThat(url.protocol()).isEqualTo("ftp:");
    Assertions.assertThat(url.href()).isEqualTo("ftp://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("ftp://www.myurl.com");
  }

  @Test
  public void setValidSearch() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.search("c=3&d=4");
    Assertions.assertThat(url.search()).isEqualTo("?c=3&d=4");
  }

  @Test
  public void setValidUsername() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.username()).isEmpty();
    url.username("ausername");
    Assertions.assertThat(url.username()).isEqualTo("ausername");
  }

  @Test
  public void validationErrors() {
    Url url = Url.create(" \t http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.validationErrors()).isNotNull();
    Assertions.assertThat(url.validationErrors().size()).isEqualTo(1);
  }
}
