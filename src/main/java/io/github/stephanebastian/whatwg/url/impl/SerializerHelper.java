package io.github.stephanebastian.whatwg.url.impl;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class SerializerHelper {
  static int compress(short[] ipPieces) {
    int result = 0;
    int largestNumberOf0 = 0;
    int currentNumberOf0 = 0;
    for (int i = 0; i < ipPieces.length; i++) {
      short ipPiece = ipPieces[i];
      if (ipPiece == 0) {
        currentNumberOf0++;
      }
      if (currentNumberOf0 > largestNumberOf0) {
        largestNumberOf0 = currentNumberOf0;
        result = i - currentNumberOf0 + 1;
      }
      if (ipPiece != 0) {
        currentNumberOf0 = 0;
      }
    }
    return largestNumberOf0 > 1 ? result : -1;
  }

  /**
   * <pre>
   *   The URL serializer takes a URL url, with an optional boolean exclude fragment (default false),
   *   and then runs these steps. They return an ASCII string.
   *   <ul>
   *     <li>1) Let output be url’s scheme and U+003A (:) concatenated.</li>
   *     <li>2) If url’s host is non-null:
   *       <ul>
   *         <li>2.1) Append "//" to output.</li>
   *         <li>2,2) If url includes credentials, then:
   *         <ul>
   *           <li>2.2.1) Append url’s username to output.</li>
   *           <li>2.2.2) If url’s password is not the empty string, then append U+003A (:),
   *           followed by url’s password, to output.</li>
   *           <li>2.2.3) Append U+0040 (@) to output.</li>
   *         </ul>
   *       </li>
   *       <li>2.3) Append url’s host, serialized, to output.</li>
   *       <li>2.4) If url’s port is non-null, append U+003A (:) followed by url’s port,
   *       serialized, to output.</li>
   *     </ul>
   *   </li>
   *   <li>3) If url’s host is null, url does not have an opaque path, url’s path’s size is greater
   *   than 1, and url’s path[0] is the empty string, then append U+002F (/) followed by U+002E (.) to
   *   output.
   *   <br>This prevents web+demo:/.//not-a-host/ or web+demo:/path/..//not-a-host/, when parsed and then
   *   serialized, from ending up as web+demo://not-a-host/ (they end up as
   *   web+demo:/.//not-a-host/).</li>
   *   <li>4) Append the result of URL path serializing url to output.</li>
   *   <li>5) If url’s query is non-null, append U+003F (?), followed by url’s query, to output.</li>
   *   <li>6) If exclude fragment is false and url’s fragment is non-null, then append U+0023 (#),
   *   followed by url’s fragment, to output.</li>
   *   <li>7) Return output.</li>
   * </ul>
   * </pre>
   *
   * @param url the url to serialize
   * @param excludeFragment a boolean indicating wether fragments are excluded
   * @param output the output of the serialization
   */
  static void serialize(UrlImpl url, boolean excludeFragment, StringBuilder output) {
    Objects.requireNonNull(url);
    Objects.requireNonNull(output);
    // 1
    output.append(url.scheme);
    output.append(":");
    // 2
    if (url.host != null) {
      // 2.1
      output.append("//");
      // 2.2
      if (url.includeCredentials()) {
        // 2.2.1
        output.append(url.username);
        // 2.2.2
        if (!url.password.isEmpty()) {
          output.append(":");
          output.append(url.password);
        }
        // 2.2.3
        output.append("@");
      }
      // 2.3
      serializeHost(url.host, output);
      // 2.4
      if (url.port != null) {
        output.append(":");
        output.append(SerializerHelper.serializeInteger(url.port));
      }
    }
    // 3
    // If url’s host is null, url does not have an opaque path, url’s path’s size is greater than 1,
    // and url’s path[0] is the empty string, then append U+002F (/) followed by U+002E (.) to
    // output.
    else if (!url.hasAnOpaquePath() && url.path.size() > 1 && "".equals(url.path.get(0))) {
      output.append("/.");
    }
    // 4
    serializePath(url, output);
    // 5
    if (url.query != null) {
      output.append("?");
      output.append(url.query);
    }
    // 7
    if (!excludeFragment && url.fragment != null) {
      output.append("#");
      output.append(url.fragment);
    }
  }

  static void serializeHost(Domain host, StringBuilder output) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(output);
    output.append(host);
  }

  static void serializeHost(OpaqueHost host, StringBuilder output) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(output);
    output.append(host);
  }

  static void serializeHost(EmptyHost host, StringBuilder output) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(output);
    output.append(host);
  }

  /**
   * The IPv4 serializer takes an IPv4 address address and then runs these steps: 1) Let output be
   * the empty string. 2) Let n be the value of address. 3) For each i in the range 1 to 4,
   * inclusive: 3.1) Prepend n % 256, serialized, to output. 3.2) If i is not 4, then prepend U+002E
   * (.) to output. 3.3) Set n to floor(n / 256). 4) Return output.
   *
   * @param host the {@link Ipv4Address} host to serialize
   * @param output the output buffer
   */
  static void serializeHost(Ipv4Address host, StringBuilder output) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(output);
    byte[] bytes = InfraHelper.getBytes(host.ip());
    output.append(SerializerHelper.serializeInteger(bytes[0] & 0x00FF));
    output.append(".");
    output.append(SerializerHelper.serializeInteger(bytes[1] & 0x00FF));
    output.append(".");
    output.append(SerializerHelper.serializeInteger(bytes[2] & 0x00FF));
    output.append(".");
    output.append(SerializerHelper.serializeInteger(bytes[3] & 0x00FF));
  }

  /**
   * The IPv6 serializer takes an IPv6 address address and then runs these steps: 1) Let output be
   * the empty string. 2) Let compress be an index to the first IPv6 piece in the first longest
   * sequences of address’s IPv6 pieces that are 0. In 0:f:0:0:f:f:0:0 it would point to the second
   * 0. 3) If there is no sequence of address’s IPv6 pieces that are 0 that is longer than 1, then
   * set compress to null. 4) Let ignore0 be false. 5) For each pieceIndex in the range 0 to 7,
   * inclusive: 5.1) If ignore0 is true and address[pieceIndex] is 0, then continue. 5.2) Otherwise,
   * if ignore0 is true, set ignore0 to false. 5.3) If compress is pieceIndex, then: 5.3.1) Let
   * separator be "::" if pieceIndex is 0, and U+003A (:) otherwise. 5.3.2) Append separator to
   * output. 5.3.3) Set ignore0 to true and continue. 5.4) Append address[pieceIndex], represented
   * as the shortest possible lowercase hexadecimal number, to output. 5.5) If pieceIndex is not 7,
   * then append U+003A (:) to output. 6) Return output.
   *
   * @param host the {@link Ipv6Address} host
   * @param output the output buffer
   */
  static void serializeHost(Ipv6Address host, StringBuilder output) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(output);
    short[] address = host.ip();
    // int compress
    int compress = compress(address);
    boolean ignore0 = false;
    for (int pieceIndex = 0; pieceIndex <= 7; pieceIndex++) {
      short ipPiece = address[pieceIndex];
      if (ignore0 && ipPiece == 0) {
        continue;
      } else if (ignore0) {
        ignore0 = false;
      }
      if (compress == pieceIndex) {
        if (pieceIndex == 0) {
          output.append("::");
        } else {
          output.append(":");
        }
        ignore0 = true;
        continue;
      }
      output.append(Integer.toHexString(ipPiece));
      if (pieceIndex != 7) {
        output.append(":");
      }
    }
  }

  /**
   * <pre>
   *   The host serializer takes a host host and then runs these steps. They return an ASCII string.
   *   <ul>
   *     <li>1) If host is an IPv4 address, return the result of running the IPv4 serializer on
   *     host.</li>
   *     <li>2) Otherwise, if host is an IPv6 address, return U+005B ([), followed by the result of
   *     running the IPv6 serializer on host, followed by U+005D (]).</li>
   *     <li>3) Otherwise, host is a domain, opaque host, or empty host, return host.</li>
   *   </ul>
   * </pre>
   *
   * @param host the host to serialize
   * @param output the output of the serialization
   */
  static void serializeHost(Host host, StringBuilder output) {
    Objects.requireNonNull(output);
    // 1
    if (host instanceof Ipv4Address) {
      serializeHost((Ipv4Address) host, output);
    }
    // 2
    else if (host instanceof Ipv6Address) {
      output.append("[");
      serializeHost((Ipv6Address) host, output);
      output.append("]");
    }
    // 3
    else if (host instanceof Domain) {
      serializeHost((Domain) host, output);
    } else if (host instanceof OpaqueHost) {
      serializeHost((OpaqueHost) host, output);
    } else if (host instanceof EmptyHost) {
      serializeHost((EmptyHost) host, output);
    }
  }

  static String serializeInteger(int value) {
    return Integer.toString(value);
  }

  /**
   * <pre>
   *   The serialization of an origin is the string obtained by applying the
   *   following algorithm to the given origin origin:
   *   <ul>
   *     <li>If origin is an opaque origin, then return "null".</li>
   *     <li>Otherwise, let result be origin's scheme.</li>
   *     <li>Append "://" to result.</li>
   *     <li>Append origin's host, serialized, to result.</li>
   *     <li>If origin's port is non-null, append a U+003A COLON character (:),
   *     and origin's port, serialized, to result.</li>
   *     <li>Return result.</li>
   *   </ul>
   *
   *   The original origin is defined below:
   *   The origin of a URL url is the origin returned by running these steps, switching on url’s scheme:
   *   <ul>
   *     <li>"blob"
   *       <ul>
   *         <li>If url’s blob URL entry is non-null, then return url’s blob
   *         URL entry’s environment’s origin.</li>
   *         <li>Let pathURL be the result of parsing the result of URL path
   *         serializing url.</li>
   *         <li>If pathURL is failure, then return a new opaque origin.</li>
   *         <li>If pathURL’s scheme is "http", "https", or "file", then return
   *         pathURL’s origin.</li>
   *         <li>Return a new opaque origin.
   *         <br>The origin of blob:https://whatwg.org/d0360e2f-caee-469f-9a2f-87d5b0456f6f
   *         is the tuple origin ("https", "whatwg.org", null, null).
   *         </li>
   *       </ul>
   *     </li>
   *     <li>"ftp", "http", "https", "ws", "wss":
   *       <ul>
   *         <li>Return the tuple origin (url’s scheme, url’s host, url’s port, null).</li>
   *       </ul>
   *     </li>
   *     <li>"file":
   *       <ul>
   *         <li>Unfortunate as it is, this is left as an exercise to the reader.
   *         When in doubt, return a new opaque origin.</li>
   *       </ul>
   *    </li>
   *    <li>Otherwise:
   *      <ul>
   *        <li>Return a new opaque origin.
   *        <br>This does indeed mean that these URLs cannot be same origin
   *        with themselves.</li></li>
   *      </ul>
   *   </ul>*
   * </pre>
   *
   * @param url the url
   * @param output the output of the serialization
   */
  static void serializeOrigin(UrlImpl url, StringBuilder output) {
    Objects.requireNonNull(url);
    Objects.requireNonNull(output);
    switch (url.scheme) {
      case "blob":
        // 2
        StringBuilder serializedPath = new StringBuilder();
        serializePath(url, serializedPath);
        try {
          UrlImpl pathUrl =
              new UrlParser().basicParse(serializedPath.toString(), null, StandardCharsets.UTF_8);
          // 4
          if ("http".equals(pathUrl.scheme) || "https".equals(pathUrl.scheme)
              || "file".equals(pathUrl.scheme)) {
            output.append(pathUrl.origin());
            return;
          }
        } catch (Exception e) {
          // 3
          output.append("null");
          return;
        }
        // 5
        output.append("null");
        return;
      case "ftp":
      case "http":
      case "https":
      case "ws":
      case "wss":
        output.append(url.scheme);
        output.append("://");
        serializeHost(url.host, output);
        if (url.port != null) {
          output.append(":");
          output.append(url.port);
        }
        return;
      case "file":
        output.append("null");
        return;
      default:
        output.append("null");
    }
  }

  /**
   * <pre>
   *   The URL path serializer takes a URL url and then runs these steps. They return an ASCII string.
   *   <ul>
   *     <li>If url has an opaque path, then return url’s path.</li>
   *     <li>Let output be the empty string.</li>
   *     <li>For each segment of url’s path: append U+002F (/) followed by segment to output.</li>
   *     <li>Return output.</li>
   *   </ul>
   * </pre>
   */
  static void serializePath(UrlImpl url, StringBuilder output) {
    if (url.hasAnOpaquePath()) {
      if (!url.path.isEmpty()) {
        output.append(url.path.get(0));
      }
      return;
    }
    for (String segment : url.path) {
      output.append("/");
      output.append(segment);
    }
  }

  /**
   * <pre>
   *   The application/x-www-form-urlencoded serializer takes a list of name-value
   *   tuples tuples, with an optional encoding encoding (default UTF-8), and
   *   then runs these steps. They return an ASCII string.
   *   <ul>
   *     <li>1) Set encoding to the result of getting an output encoding from encoding.</li>
   *     <li>2) Let output be the empty string.</li>
   *     <li>3) For each tuple of tuples:
   *       <ul>
   *         <li>3.1) Assert: tuple’s name and tuple’s value are scalar value strings.</li>
   *         <li>3.2) Let name be the result of running percent-encode after encoding
   *         with encoding, tuple’s name, the application/x-www-form-urlencoded
   *         percent-encode set, and true.</li>
   *         <li>3.3) Let value be the result of running percent-encode after encoding with encoding,
   *         tuple’s value, the application/x-www-form-urlencoded percent-encode set, and true.</li>
   *         <li>3.4) If output is not the empty string, then append U+0026 (&) to output.</li>
   *         <li>3.5) Append name, followed by U+003D (=), followed by value, to output.</li>
   *       </ul>
   *     <li>Return output.</li>
   *   </ul>
   * </pre>
   *
   * @param tuples tuples to encode
   * @param output the output buffer
   */
  static void serializeFormUrlEncoded(List<UrlSearchParam> tuples, CharsetEncoder encoder,
      StringBuilder output) {
    Objects.requireNonNull(tuples);
    Objects.requireNonNull(encoder);
    Objects.requireNonNull(output);
    for (UrlSearchParam tuple : tuples) {
      String encodedName = UrlHelper.percentEncodeAfterEncoding(encoder, tuple.name(),
          CodepointHelper::isInUrlEncodedPercentEncodeSet, true);
      String encodedValue = UrlHelper.percentEncodeAfterEncoding(encoder, tuple.value(),
          CodepointHelper::isInUrlEncodedPercentEncodeSet, true);
      if (output.length() > 0) {
        output.append('&');
      }
      output.append(encodedName);
      output.append('=');
      output.append(encodedValue);
    }
  }

  /**
   * The application/x-www-form-urlencoded byte serializer takes a byte sequence input and then runs
   * these steps: Let output be the empty string. For each byte in input, depending on byte: 0x20
   * (SP) Append U+002B (+) to output. 0x2A (*) 0x2D (-) 0x2E (.) 0x30 (0) to 0x39 (9) 0x41 (A) to
   * 0x5A (Z) 0x5F (_) 0x61 (a) to 0x7A (z) Append a code point whose value is byte to output.
   * Otherwise Append byte, percent-encoded, to output. Return output.
   *
   * @param bytes the bytes to encode
   * @param output the output of the serialization
   */
  static void urlEncodedSerialize(byte[] bytes, StringBuilder output) {
    for (byte abyte : bytes) {
      if (abyte == 0x20) {
        output.append("+");
      } else if (abyte == 0x2A || abyte == 0x2D || abyte == 0x2E || InfraHelper.isAsciiDigit(abyte)
          || InfraHelper.isAsciiUpperAlpha(abyte) || abyte == 0x5F
          || InfraHelper.isAsciiLowerAlpha(abyte)) {
        output.appendCodePoint(abyte);
      } else {
        output.append(UrlHelper.percentEncode(abyte));
      }
    }
  }
}
