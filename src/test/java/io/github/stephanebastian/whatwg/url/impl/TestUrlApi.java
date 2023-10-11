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
  }

  @Test
  public void parseUrlWithValidationError() {
    Url url = Url.create(" \t http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https://127.0.0.1./");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_EMPTY_PART);
    url = Url.create("https://127.0.0x0.1");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_NON_DECIMAL_PART);
    url = Url.create("https://example.org/>");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create(" https://example.org ");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("ht\ntps://example.org");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https://example.org/%s");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https:example.org");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.SPECIAL_SCHEME_MISSING_FOLLOWING_SOLIDUS);
    url = Url.create("https://example.org\\\\path\\\\to\\\\file\\");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    url = Url.create("https://user@example.org");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_CREDENTIALS);
    url = Url.create("ssh://user@example.org");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_CREDENTIALS);
    // url = Url.create("//c:/path/to/file", "file:///c:/");
    // Assertions.assertThat(url.validationErrors()).element(0).isEqualTo(ValidationError.FILE_INVALID_WINDOWS_DRIVE_LETTER);
    url = Url.create("file://c:");
    Assertions.assertThat(url.validationErrors()).element(0)
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
  public void setHash() {
    Url url = Url.create();
    // with leading #
    url.hash("#hash1");
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // without leading #
    url = Url.create();
    url.hash("hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // with percent encoded value
    url = Url.create();
    url.hash("%20hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#%20hash2");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // with leading # and percent encoded value
    url = Url.create();
    url.hash("#%20hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#%20hash2");
    Assertions.assertThat(url.validationErrors()).isEmpty();
  }

  @Test
  public void setHashWithValidationErrors() {
    Url url = Url.create();
    // bad url codepoint
    url.hash("hash3 ");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).first()
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.hash()).isEqualTo("#hash3%20");
    // bad url codepoint
    url = Url.create();
    url.hash(" hash3 ");
    Assertions.assertThat(url.validationErrors()).hasSize(2);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.validationErrors()).element(1)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.hash()).isEqualTo("#%20hash3%20");
    // bad url codepoint
    url = Url.create();
    url.hash(" #hash3 ");
    Assertions.assertThat(url.validationErrors()).hasSize(3);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.validationErrors()).element(1)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.validationErrors()).element(2)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.hash()).isEqualTo("#%20#hash3%20");
    // bad url codepoint
    url = Url.create();
    url.hash("%h");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    Assertions.assertThat(url.hash()).isEqualTo("#%h");
  }

  @Test
  public void setHost() {
    // opaque host
    Url url = Url.create();
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("anotherhost.io");
    Assertions.assertThat(url.host()).isEqualTo("anotherhost.io");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // opaque host with percent encoded
    url = Url.create();
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("another%20host.io");
    Assertions.assertThat(url.host()).isEqualTo("another%20host.io");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // domain
    url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("anotherhost.io");
    Assertions.assertThat(url.host()).isEqualTo("anotherhost.io");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // ipv4
    url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("192.168.0.1");
    Assertions.assertThat(url.host()).isEqualTo("192.168.0.1");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // ipv6
    url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("[2001:0db8:0000:85a3:0000:0000:ac1f:8001]");
    Assertions.assertThat(url.host()).isEqualTo("[2001:db8:0:ffff85a3::ffffac1f:ffff8001]");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    // short ipv6
    url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("[fe00::1]");
    Assertions.assertThat(url.host()).isEqualTo("[fffffe00::1]");
    Assertions.assertThat(url.validationErrors()).isEmpty();
  }

  @Test
  public void setHostWithValidationError() {
    // opaque host with an invalid codepoint
    Url url = Url.create();
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("another%host.io");
    Assertions.assertThat(url.host()).isEqualTo("another%host.io");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    // ipv4 with an empty part
    url = Url.create("https://host.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("127.0.0.1.");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_EMPTY_PART);
    Assertions.assertThat(url.host()).isEqualTo("127.0.0.1");
    // ipv4 with non-decimal part
    url = Url.create("https://host.com");
    url.host("127.0.0x0.1");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_NON_DECIMAL_PART);
    Assertions.assertThat(url.host()).isEqualTo("127.0.0.1");
    // ipv4 out of range part
    url = Url.create("https://host.com");
    url.host("255.255.4000");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_OUT_OF_RANGE_PART);
    Assertions.assertThat(url.host()).isEqualTo("255.255.15.160");
  }

  @Test
  public void setHostWithValidationFailure() {
    // opaque host with an invalid codepoint
    Url url = Url.create();
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("exa[mple.org");
    Assertions.assertThat(url.host()).isEqualTo("");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.HOST_INVALID_CODEPOINT);
    // domain with invalid codepoint
    url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("exa%23mple.org");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.DOMAIN_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    // ipv4 too many parts
    url = Url.create("https://host.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.host("1.2.3.4.5");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_TOO_MANY_PARTS);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 non numeric part
    url = Url.create("https://host.com");
    url.host("test.42");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_NON_NUMERIC_PART);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 out of range part
    url = Url.create("https://host.com");
    url.host("255.255.4000.4000");
    Assertions.assertThat(url.validationErrors()).hasSize(2);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_OUT_OF_RANGE_PART);
    Assertions.assertThat(url.validationErrors()).element(1)
        .isEqualTo(ValidationError.IPV4_OUT_OF_RANGE_PART);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 unclosed
    url = Url.create("https://host.com");
    url.host("[::1");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_UNCLOSED);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 invalid compression
    url = Url.create("https://host.com");
    url.host("[:1]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_INVALID_COMPRESSION);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 too many pieces
    url = Url.create("https://host.com");
    url.host("[1:2:3:4:5:6:7:8:9]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_TOO_MANY_PIECES);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 too many pieces
    url = Url.create("https://host.com");
    url.host("[1::1::1]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_MULTIPLE_COMPRESSION);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 invalid codepoint
    url = Url.create("https://host.com");
    url.host("[1:2:3!:4]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 invalid codepoint 2
    url = Url.create("https://host.com");
    url.host("[1:2:3:]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv6 too few pieces
    url = Url.create("https://host.com");
    url.host("[1:2:3]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV6_TOO_FEW_PIECES);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 invalid codepoint
    url = Url.create("https://host.com");
    url.host("[ffff::.0.0.1]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 invalid codepoint 2
    url = Url.create("https://host.com");
    url.host("[ffff::127.0.xyz.1]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 invalid codepoint 3
    url = Url.create("https://host.com");
    url.host("[ffff::127.0xyz]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 invalid codepoint 4
    url = Url.create("https://host.com");
    url.host("[ffff::127.00.0.1]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 invalid codepoint 5
    url = Url.create("https://host.com");
    url.host("[ffff::127.0.0.1.2]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_INVALID_CODEPOINT);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 out of range part
    url = Url.create("https://host.com");
    url.host("[ffff::127.0.0.4000]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_OUT_OF_RANGE_PART);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
    // ipv4 in ipv6 out of range part
    url = Url.create("https://host.com");
    url.host("[ffff::127.0.0]");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_IN_IPV6_TOO_FEW_PARTS);
    Assertions.assertThat(url.host()).isEqualTo("host.com");
  }

  @Test
  public void setHostname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.hostname("anotherhost.io");
    Assertions.assertThat(url.hostname()).isEqualTo("anotherhost.io");
  }

  @Test
  public void setHostnameWithValidationError() {
    Url url = Url.create("https://host.com");
    url.hostname("127.0.0.1.");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_EMPTY_PART);
    url = Url.create("https://host.com");
    url.hostname("127.0.0x0.1");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.IPV4_NON_DECIMAL_PART);
  }

  @Test
  public void setHref() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
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
  public void setPassword() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.password("password2");
    Assertions.assertThat(url.password()).isEqualTo("password2");
  }

  @Test
  public void setPathname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.pathname("path2/path3");
    Assertions.assertThat(url.pathname()).isEqualTo("/path2/path3");
    url.pathname("/path4");
    Assertions.assertThat(url.pathname()).isEqualTo("/path4");
  }

  @Test
  public void setPathnameWithValidationError() {
    Url url = Url.create();
    url.pathname(">");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create();
    url.pathname(" ");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create();
    url.pathname("%s");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create("https://example.org");
    url.pathname("\\\\path\\\\to\\\\file\\");
    Assertions.assertThat(url.validationErrors()).hasSize(7);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(1)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(2)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(3)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(4)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(5)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
    Assertions.assertThat(url.validationErrors()).element(6)
        .isEqualTo(ValidationError.INVALID_REVERSE_SOLIDUS);
  }

  @Test
  public void setPort() {
    Url url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    url.port("443");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    Assertions.assertThat(url.port()).isEqualTo("443");
    url.port("80");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    Assertions.assertThat(url.port()).isEqualTo("");
    url.port("8080");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    Assertions.assertThat(url.port()).isEqualTo("8080");
    url.port("804zzz");
    Assertions.assertThat(url.validationErrors()).isEmpty();
    Assertions.assertThat(url.port()).isEqualTo("804");
  }

  @Test
  public void setPortwithValidationFailure() {
    Url url = Url.create("http://www.myurl.com");
    Assertions.assertThat(url.port()).isEqualTo("");
    url.port("70000");
    Assertions.assertThat(url.validationErrors()).hasSize(1);
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.PORT_OUT_OF_RANGE);
  }

  @Test
  public void setProtocol() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.protocol("ftp");
    Assertions.assertThat(url.protocol()).isEqualTo("ftp:");
    Assertions.assertThat(url.href()).isEqualTo("ftp://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("ftp://www.myurl.com");
  }

  @Test
  public void setProtocolWithValidationError() {
    Url url = Url.create();
    url.protocol("ht\ntps");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
    url = Url.create();
    url.protocol("ht tps");
    Assertions.assertThat(url.validationErrors()).element(0)
        .isEqualTo(ValidationError.INVALID_URL_UNIT);
  }

  @Test
  public void setSearch() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.search("c=3&d=4");
    Assertions.assertThat(url.search()).isEqualTo("?c=3&d=4");
  }

  @Test
  public void setUsername() {
    Url url = Url.create();
    Assertions.assertThat(url.username()).isEmpty();
    // we can not set the username on a url without a host
    url.username("username");
    Assertions.assertThat(url.username()).isEmpty();
    // we can not set the username if the host is file
    url = Url.create("file://");
    url.username("username");
    Assertions.assertThat(url.username()).isEmpty();
    // set basic username
    url = Url.create("http://myhost.com");
    url.username("username");
    Assertions.assertThat(url.username()).isEqualTo("username");
    // set username - percent encoded result
    url = Url.create("http://myhost.com");
    url.username("user name");
    Assertions.assertThat(url.username()).isEqualTo("user%20name");
  }
}
