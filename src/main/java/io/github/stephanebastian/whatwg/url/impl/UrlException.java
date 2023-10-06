package io.github.stephanebastian.whatwg.url.impl;

public class UrlException extends RuntimeException {
  public final static UrlException DOMAIN_TO_ASCII = new UrlException(
      "Unicode ToASCII records an error or returns the empty string. [UTS46]\n, If details about Unicode ToASCII errors are recorded, user agents are encouraged to pass those along.");
  public final static UrlException DOMAIN_TO_UNICODE =
      new UrlException("Unicode ToUnicode records an error. [UTS46]\n" + "\n"
          + "The same considerations as with domain-to-ASCII apply. ");
  public final static UrlException DOMAIN_INVALID_CODEPOINT =
      new UrlException("The input’s host contains a forbidden domain code point.\n" + "\n"
          + "Hosts are percent-decoded before being processed when the URL is special, which would result in the following host portion becoming \"exa#mple.org\" and thus triggering this error.\n"
          + "\n" + "\"https://exa%23mple.org\"\n");
  public final static UrlException HOST_INVALID_CODEPOINT = new UrlException(
      "An opaque host (in a URL that is not special) contains a forbidden host code point.\n" + "\n"
          + "\"foo://exa[mple.org\" ");
  public final static UrlException IPV4_EMPTY_PART = new UrlException(
      "An IPv4 address ends with a U+002E (.).\n" + "\n" + "\"https://127.0.0.1./\" ");
  public final static UrlException IPV4_TOO_MANY_PARTS = new UrlException(
      "An IPv4 address does not consist of exactly 4 parts.\n" + "\n" + "\"https://1.2.3.4.5/\" ");
  public final static UrlException IPV4_NON_NUMERIC_PART =
      new UrlException("An IPv4 address part is not numeric.\n" + "\n" + "\"https://test.42\" ");
  public final static UrlException IPV4_NON_DECIMAL_PART = new UrlException(
      "The IPv4 address contains numbers expressed using hexadecimal or octal digits.\n" + "\n"
          + "\"https://127.0.0x0.1\" ");
  public final static UrlException IPV4_OUT_OF_RANGE_PART = new UrlException(
      "An IPv4 address part exceeds 255.\n" + "\n" + "\"https://255.255.4000.1\" ");
  public final static UrlException IPV6_UNCLOSED = new UrlException(
      "An IPv6 address is missing the closing U+005D (]).\n" + "\n" + "\"https://[::1\" ");
  public final static UrlException IPV6_INVALID_COMPRESSION = new UrlException(
      "An IPv6 address begins with improper compression.\n" + "\n" + "\"https://[:1]\" ");
  public final static UrlException IPV6_TOO_MANY_PIECES = new UrlException(
      "An IPv6 address contains more than 8 pieces.\n" + "\n" + "\"https://[1:2:3:4:5:6:7:8:9]\" ");
  public final static UrlException IPV6_MULTIPLE_COMPRESSION = new UrlException(
      "An IPv6 address is compressed in more than one spot.\n" + "\n" + "\"https://[1::1::1]\" ");
  public final static UrlException IPV6_INVALID_CODEPOINT = new UrlException(
      "An IPv6 address contains a code point that is neither an ASCII hex digit nor a U+003A (:). Or it unexpectedly ends.\n"
          + "\n" + "\"https://[1:2:3!:4]\"\n" + "\n" + "\"https://[1:2:3:]\"\n");
  public final static UrlException IPV6_TOO_FEW_PIECES =
      new UrlException("An uncompressed IPv6 address contains fewer than 8 pieces.\n" + "\n"
          + "\"https://[1:2:3]\" ");
  public final static UrlException IPV4_IPV6_TOO_MANY_PIECES = new UrlException(
      "An IPv6 address with IPv4 address syntax: the IPv6 address has more than 6 pieces.\n" + "\n"
          + "\"https://[1:1:1:1:1:1:1:127.0.0.1]\"");
  public final static UrlException IPV4_IPV6_INVALID_CODEPOINT =
      new UrlException("An IPv6 address with IPv4 address syntax:\n" + "\n"
          + "    An IPv4 part is empty or contains a non-ASCII digit.\n"
          + "    An IPv4 part contains a leading 0.\n" + "    There are too many IPv4 parts. \n"
          + "\n" + "\"https://[ffff::.0.0.1]\"\n" + "\n" + "\"https://[ffff::127.0.xyz.1]\"\n"
          + "\n" + "\"https://[ffff::127.0xyz]\"\n" + "\n" + "\"https://[ffff::127.00.0.1]\"\n"
          + "\n" + "\"https://[ffff::127.0.0.1.2]\"\n");
  public final static UrlException IPV4_IPV6_OUT_OF_RANGE_PART =
      new UrlException("An IPv6 address with IPv4 address syntax: an IPv4 part exceeds 255.\n"
          + "\n" + "\"https://[ffff::127.0.0.4000]\" ");
  public final static UrlException IPV4_IN_IPV6_TOO_FEW_PARTS = new UrlException(
      "An IPv6 address with IPv4 address syntax: an IPv4 address contains too few parts.\n" + "\n"
          + "\"https://[ffff::127.0.0]\" ");
  public final static UrlException INVALID_URL_UNIT =
      new UrlException("A code point is found that is not a URL unit.\n" + "\n"
          + "\"https://example.org/>\"\n" + "\n" + "\" https://example.org \"\n" + "\n" + "\"ht\n"
          + "tps://example.org\"\n" + "\n" + "\"https://example.org/%s\"\n");
  public final static UrlException SPECIAL_SCHEME_MISSING_FOLLOWING_SOLIDUS =
      new UrlException("The input’s scheme is not followed by \"//\".\n" + "\n"
          + "\"file:c:/my-secret-folder\"\n" + "\n" + "\"https:example.org\"\n" + "\n"
          + "const url = new URL(\"https:foo.html\", \"https://example.org/\");\n" + "\n");
  public final static UrlException MISSING_SCHEME_NON_RELATIVE_URL = new UrlException(
      "The input is missing a scheme, because it does not begin with an ASCII alpha, and either no base URL was provided or the base URL cannot be used as a base URL because it has an opaque path.\n"
          + "\n" + "Input’s scheme is missing and no base URL is given:\n" + "\n"
          + "const url = new URL(\"\uD83D\uDCA9\");\n" + "\n"
          + "Input’s scheme is missing, but the base URL has an opaque path.\n" + "\n"
          + "const url = new URL(\"\uD83D\uDCA9\", \"mailto:user@example.org\");\n" + "\n");
  public final static UrlException INVALID_REVERSE_SOLIDUS = new UrlException(
      "The URL has a special scheme and it uses U+005C (\\) instead of U+002F (/).\n" + "\n"
          + "\"https://example.org\\path\\to\\file\" ");
  public final static UrlException INVALID_CREDENTIALS =
      new UrlException("The input includes credentials.\n" + "\n" + "\"https://user@example.org\"\n"
          + "\n" + "\"https://user:pass@\"\n");
  public final static UrlException HOST_MISSING =
      new UrlException("The input has a special scheme, but does not contain a host.\n" + "\n"
          + "\"https://#fragment\"\n" + "\n" + "\"https://:443\"\n");
  public final static UrlException PORT_OUT_OF_RANGE =
      new UrlException("The input’s port is too big.\n" + "\n" + "\"https://example.org:70000\" ");
  public final static UrlException PORT_INVALID =
      new UrlException("The input’s port is invalid.\n" + "\n" + "\"https://example.org:7z\" ");
  public final static UrlException FILE_INVALID_WINDOWS_DRIVE_LETTER = new UrlException(
      "The input is a relative-URL string that starts with a Windows drive letter and the base URL’s scheme is \"file\".\n"
          + "\n" + "const url = new URL(\"/c:/path/to/file\", \"file:///c:/\");");
  public final static UrlException FILE_INVALID_WINDOWS_DRIVE_LETTER_HOST =
      new UrlException("A file: URL’s host is a Windows drive letter.\n" + "\n" + "\"file://c:\" ");
  public final static UrlException INVALID_SCHEME = new UrlException("The scheme is invalid");
  public final static UrlException _IPV4_NUMBER_PARSER = new UrlException(
      "The input can't be parsed to an IPV4 number - Note that this exception is not defined by the WhatWg specification.\n"
          + "\n" + "\"file://c:\" ");
  public final static UrlException _SEARCH_PARAMS_INIT =
      new UrlException("Search parameters can´t be instantiated");

  private UrlException(String message) {
    super(message);
  }
}
