package io.github.stephanebastian.whatwg.url;

import java.util.Objects;

public enum ValidationError {
  // @formatter:off
  DOMAIN_TO_ASCII(
 "Unicode ToASCII records an error or returns the empty string. [UTS46]\n"
       + ", If details about Unicode ToASCII errors are recorded, user agents are encouraged to pass those along.",
 true),
  DOMAIN_TO_UNICODE(
 "Unicode ToUnicode records an error. [UTS46]\n"
       + "\n"
       + "The same considerations as with domain-to-ASCII apply. ",
 false),
  DOMAIN_INVALID_CODEPOINT(
 "The input’s host contains a forbidden domain code point.\n"
       + "\nHosts are percent-decoded before being processed when the URL is special, which would result in the following host portion becoming \"exa#mple.org\" and thus triggering this error.\n"
       + "\n"
       + "\"https://exa%23mple.org\"",
 true),
  HOST_INVALID_CODEPOINT(
 "An opaque host (in a URL that is not special) contains a forbidden host code point.\n"
       + "\n"
       + "\"foo://exa[mple.org\" ",
 true),
  IPV4_EMPTY_PART(
 "An IPv4 address ends with a U+002E (.).\n"
       + "\n"
       + "\"https://127.0.0.1./\" ",
 false),
  IPV4_TOO_MANY_PARTS(
 "An IPv4 address does not consist of exactly 4 parts.\n"
       + "\n"
       + "\"https://1.2.3.4.5/\" ",
 true),
  IPV4_NON_NUMERIC_PART(
 "An IPv4 address part is not numeric.\n"
       + "\n"
       + "\"https://test.42\" ",
 true),
  IPV4_NON_DECIMAL_PART(
 "The IPv4 address contains numbers expressed using hexadecimal or octal digits.\n"
       + "\n"
       + "\"https://127.0.0x0.1\" ",
 false),
  IPV4_OUT_OF_RANGE_PART(
 "An IPv4 address part exceeds 255.\n"
       + "\n"
       + "\"https://255.255.4000.1\" ",
 true),
  IPV6_UNCLOSED(
 "An IPv6 address is missing the closing U+005D (]).\n"
       + "\n"
       + "\"https://[::1\" ",
 true),
  IPV6_INVALID_COMPRESSION(
 "An IPv6 address begins with improper compression.\n"
       + "\n" + "\"https://[:1]\" ",
 true),
  IPV6_TOO_MANY_PIECES(
 "An IPv6 address contains more than 8 pieces.\n"
       + "\n"+ "\"https://[1:2:3:4:5:6:7:8:9]\" ",
 true),
  IPV6_MULTIPLE_COMPRESSION(
 "An IPv6 address is compressed in more than one spot.\n"
       + "\n"+ "\"https://[1::1::1]\" ",
 true),
  IPV6_INVALID_CODEPOINT(
 "An IPv6 address contains a code point that is neither an ASCII hex digit nor a U+003A (:). Or it unexpectedly ends.\n"
       + "\n"+ "\"https://[1:2:3!:4]\"\n"
       + "\n"+ "\"https://[1:2:3:]\"\n",
 true),
  IPV6_TOO_FEW_PIECES(
 "An uncompressed IPv6 address contains fewer than 8 pieces.\n"
       + "\n"+ "\"https://[1:2:3]\" ",
 true),
  IPV4_IN_IPV6_TOO_MANY_PIECES(
 "An IPv6 address with IPv4 address syntax: the IPv6 address has more than 6 pieces.\n"
       + "\n"+ "\"https://[1:1:1:1:1:1:1:127.0.0.1]\"",
 true),
  IPV4_IN_IPV6_INVALID_CODEPOINT(
 "An IPv6 address with IPv4 address syntax:\n"
       + "\n"
       + "An IPv4 part is empty or contains a non-ASCII digit.\n"
       + "An IPv4 part contains a leading 0.\n"
       + "There are too many IPv4 parts. \n"
       + "\n"
       + "\"https://[ffff::.0.0.1]\"\n"
       + "\n"
       + "\"https://[ffff::127.0.xyz.1]\"\n"
       + "\n"
       + "\"https://[ffff::127.0xyz]\"\n"
       + "\n"
       + "\"https://[ffff::127.00.0.1]\"\n"
       + "\n"
       + "\"https://[ffff::127.0.0.1.2]\"\n",
true),
  IPV4_IN_IPV6_OUT_OF_RANGE_PART(
 "An IPv6 address with IPv4 address syntax: an IPv4 part exceeds 255.\n"
       + "\n"
       + "\"https://[ffff::127.0.0.4000]\" ",
 true),
  IPV4_IN_IPV6_TOO_FEW_PARTS(
 "An IPv6 address with IPv4 address syntax: an IPv4 address contains too few parts.\n"
       + "\n"
       + "\"https://[ffff::127.0.0]\" ",
 true),
  INVALID_URL_UNIT(
 "A code point is found that is not a URL unit.\n"
       + "\n"
       + "\"https://example.org/>\"\n"
       + "\n"
       + "\" https://example.org \"\n"
       + "\n"
       + "\"https://example.org\"\n"
       + "\n"
       + "\"https://example.org/%s\"",
 false),
  SPECIAL_SCHEME_MISSING_FOLLOWING_SOLIDUS(
 "The input’s scheme is not followed by \"//\".\n"
       + "\n"
       + "\"file:c:/my-secret-folder\"\n"
       + "\n"
       + "\"https:example.org\"\n"
       + "\n"
       + "const url = new URL(\"https:foo.html\", \"https://example.org/\"),\n",
  false),
  MISSING_SCHEME_NON_RELATIVE_URL(
 "The input is missing a scheme, because it does not begin with an ASCII alpha, and either no base URL was provided or the base URL cannot be used as a base URL because it has an opaque path.\n"
       + "\n"
       + "Input’s scheme is missing and no base URL is given:\n"
       + "\n"
       + "const url = new URL(\"\uD83D\uDCA9\"),\n"
       + "\n"
       + "Input’s scheme is missing, but the base URL has an opaque path.\n"
       + "\n"
       + "const url = new URL(\"\uD83D\uDCA9\", \"mailto:user@example.org\"),\n",
 true),
  INVALID_REVERSE_SOLIDUS(
 "The URL has a special scheme and it uses U+005C (\\) instead of U+002F (/).\n"
       + "\n"
       + "\"https://example.org\\path\\to\\file\" ",
 false),
  INVALID_CREDENTIALS(
 "The input includes credentials.\n"
       + "\n"
       + "\"https://user@example.org\"\n"
       + "\n"
       + "\"ssh://user@example.org\"\n",
 false),
  HOST_MISSING(
 "The input has a special scheme, but does not contain a host.\n"
       + "\n"
       + "\"https://#fragment\"\n"
       + "\n"
       + "\"https://:443\"\n",
 true),
  PORT_OUT_OF_RANGE(
 "The input’s port is too big.\n"
       + "\n"
       + "\"https://example.org:70000\" ",
 true),
  PORT_INVALID(
 "The input’s port is invalid.\n"
       + "\n"
       + "\"https://example.org:7z\" ",
 true),
  FILE_INVALID_WINDOWS_DRIVE_LETTER(
 "The input is a relative-URL string that starts with a Windows drive letter and the base URL’s scheme is \"file\".\n"
       + "\n"
       + "const url = new URL(\"c:/path/to/file\", \"file:///c:/\"),",
 false),
  FILE_INVALID_WINDOWS_DRIVE_LETTER_HOST(
 "A file: URL’s host is a Windows drive letter.\n"
       + "\n"
       + "\"file://c:\" ",
 false),
  _INVALID_SCHEME(
 "The scheme is invalid",
 true),
  _IPV4_NUMBER_PARSER(
 "The input can't be parsed to an IPV4 number - Note that this exception is not defined by the WhatWg specification.\n"
       + "\n"
       + "\"file://c:\" ",
 true),
  _SEARCH_PARAMS_INIT(
 "Search parameters can´t be instantiated",
 true);
  // @formatter:on
  private final String description;
  private final boolean isFailure;

  ValidationError(String description, boolean isFailure) {
    this.description = Objects.requireNonNull(description);
    this.isFailure = isFailure;
  }

  public String description() {
    return description;
  }

  public boolean isFailure() {
    return isFailure;
  }
}
