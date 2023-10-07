package io.github.stephanebastian.whatwg.url.impl;

import com.ibm.icu.text.IDNA;
import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;

class UrlHelper {
  static IDNA uts46NonStrictInstance = IDNA.getUTS46Instance(IDNA.CHECK_BIDI | IDNA.CHECK_CONTEXTJ
      | /* Transitional_Processing set to false */ IDNA.NONTRANSITIONAL_TO_ASCII
      | IDNA.NONTRANSITIONAL_TO_UNICODE);
  static IDNA uts46strictInstance = IDNA.getUTS46Instance(IDNA.CHECK_BIDI | IDNA.CHECK_CONTEXTJ
      | /* Transitional_Processing set to false */ IDNA.NONTRANSITIONAL_TO_ASCII
      | IDNA.NONTRANSITIONAL_TO_UNICODE | /* strict set to true */ IDNA.USE_STD3_RULES);

  public static int codePoint(CharSequence input, int pointer) {
    Objects.requireNonNull(input);
    if (pointer >= 0 && pointer < input.length()) {
      return Character.codePointAt(input, pointer);
    }
    if (input.length() == 0) {
      return CodepointHelper.CP_EOF;
    }
    if (pointer >= input.length()) {
      return CodepointHelper.CP_EOF;
    }
    return CodepointHelper.CP_BOF;
  }

  /**
   *
   */
  public static int[] codepoints(String value) {
    Objects.requireNonNull(value);
    return value.codePoints().toArray();
  }

  /**
   * <pre>
   *   The domain to ASCII algorithm, given a string domain and a boolean beStrict, runs these steps:
   *   <ul>
   *     <li>1) Let result be the result of running Unicode ToASCII with
   *     domain_name set to domain,
   *     UseSTD3ASCIIRules set to beStrict,
   *     CheckHyphens set to false,
   *     CheckBidi set to true,
   *     CheckJoiners set to true,
   *     Transitional_Processing set to false,
   *     and VerifyDnsLength set to beStrict. [UTS46]</li>
   *     <br>
   *     <br>If beStrict is false, domain is an ASCII string, and strictly splitting
   *     domain on U+002E (.) does not produce any item that starts with an ASCII
   *     case-insensitive match for "xn--", this step is equivalent to ASCII lowercasing domain.</li>
   *     <li>2) If result is a failure value, domain-to-ASCII validation error, return failure.</li>
   *     <li>3) If result is the empty string, domain-to-ASCII validation error, return failure.</li>
   *     <li>4) Return result.</li>
   *   </ul>
   * </pre>
   *
   * @param domain the domain to convert
   * @param beStrict a boolean set to true to perform a strict conversion
   * @return the converted value
   */
  public static String domainToAscii(String domain, boolean beStrict) {
    // special case for domain with empty labels
    if (".".equals(domain) || "..".equals(domain)) {
      return domain;
    }
    // 1
    // we've got to use UTR46 from ICU4J otherwise, IDN built-in java choke on some domain names
    StringBuilder result = new StringBuilder(domain.length());
    IDNA.Info idnaInfo = new IDNA.Info();
    if (beStrict) {
      uts46strictInstance.nameToASCII(domain, result, idnaInfo);
    } else {
      uts46NonStrictInstance.nameToASCII(domain, result, idnaInfo);
    }
    // 2
    for (IDNA.Error error : idnaInfo.getErrors()) {
      // equivalent to checkHyphens==false
      if (IDNA.Error.HYPHEN_3_4.equals(error) || IDNA.Error.LEADING_HYPHEN.equals(error)
          || IDNA.Error.TRAILING_HYPHEN.equals(error)) {
        continue;
      }
      // in non-strict mode let ignore label-too-long or domain-too-long
      if (!beStrict && (IDNA.Error.DOMAIN_NAME_TOO_LONG.equals(error)
          || IDNA.Error.LABEL_TOO_LONG.equals(error))) {
        continue;
      }
      // lets ignore empty labels
      if (IDNA.Error.EMPTY_LABEL.equals(error)) {
        continue;
      }
      throw UrlException.DOMAIN_TO_ASCII;
    }
    // 3
    if (result.length() == 0) {
      throw UrlException.DOMAIN_TO_ASCII;
    }
    // 4
    return result.toString();
  }

  /**
   * <pre>
   *   The domain to Unicode algorithm, given a domain domain and a boolean beStrict, runs these steps:
   *   <ul>
   *     <li>1) Let result be the result of running Unicode ToUnicode with
   *     domain_name set to domain,
   *     CheckHyphens set to false,
   *     CheckBidi set to true,
   *     CheckJoiners set to true,
   *     UseSTD3ASCIIRules set to beStrict,
   *     and Transitional_Processing set to false. [UTS46]
   *     </li>
   *     <li>2) Signify domain-to-Unicode validation errors for any returned errors, and then, return result.</li>
   *   </ul>
   * </pre>
   *
   * @param domain the domain to convert
   * @param beStrict a boolean set to true to perform a strict conversion
   * @return the converted value
   */
  public static String domainToUnicode(String domain, boolean beStrict) {
    try {
      // 1
      // we've got to use UTR46 from ICU4J otherwise, IDN built-in java choke on some domain names
      StringBuilder result = new StringBuilder(domain.length());
      IDNA.Info idnaInfo = new IDNA.Info();
      if (beStrict) {
        uts46strictInstance.nameToUnicode(domain, result, idnaInfo);
      } else {
        uts46NonStrictInstance.nameToUnicode(domain, result, idnaInfo);
      }
      if (result.length() == 0) {
        throw UrlException.DOMAIN_TO_ASCII;
      }
      // 4
      return result.toString();
    } catch (Exception e) {
      // 2
      throw UrlException.DOMAIN_TO_ASCII;
    }
  }

  public static Integer getDefaultSchemePort(String scheme) {
    switch (scheme) {
      case "ftp":
        return 21;
      case "http":
        return 80;
      case "https":
        return 443;
      case "ws":
        return 80;
      case "wss":
        return 443;
    }
    return null;
  }

  public static boolean hasAsciiTabOrNewline(int[] codepoints) {
    return numberOfAsciiTabOrNewline(codepoints) > 0;
  }

  public static boolean hasLeadingOrTrailingC0ControlOrSpace(int[] codepoints) {
    return codepoints != null && codepoints.length > 0
        && (InfraHelper.isC0ControlOrSpace(codepoints[0])
            || InfraHelper.isC0ControlOrSpace(codepoints[codepoints.length - 1]));
  }

  /**
   * A double-dot path segment must be ".." or an ASCII case-insensitive match for ".%2e", "%2e.",
   * or "%2e%2e".
   *
   * @return
   */
  public static boolean isDoubleDotPathSegment(CharSequence value) {
    return "..".contentEquals(value) || ".%2e".contentEquals(value) || ".%2E".contentEquals(value)
        || "%2e.".contentEquals(value) || "%2E.".contentEquals(value)
        || "%2e%2e".contentEquals(value) || "%2e%2E".contentEquals(value)
        || "%2E%2e".contentEquals(value) || "%2E%2E".contentEquals(value);
  }

  /**
   * A normalized Windows drive letter is a Windows drive letter of which the second code point is
   * U+003A (:).
   *
   * @return
   */
  public static boolean isNormalizedWindowsDriveLetter(String value) {
    return value.length() == 2 && isWindowsDriveLetter(value, 0) && value.codePointAt(1) == 0x003A;
  }

  /**
   * A single-dot path segment must be "." or an ASCII case-insensitive match for "%2e".
   *
   * @return
   */
  public static boolean isSingleDotPathSegment(CharSequence value) {
    return ".".contentEquals(value) || "%2e".contentEquals(value) || "%2E".contentEquals(value);
  }

  public static boolean isSpecialScheme(CharSequence scheme) {
    return "ftp".contentEquals(scheme) || "file".contentEquals(scheme)
        || "http".contentEquals(scheme) || "https".contentEquals(scheme)
        || "ws".contentEquals(scheme) || "wss".contentEquals(scheme);
  }

  public static boolean isWindowsDriveLetter(int cp1, int cp2) {
    return InfraHelper.isAsciiAlpha(cp1) && (cp2 == 0x003A || cp2 == 0x007C);
  }

  /**
   * A Windows drive letter is two code points, of which the first is an ASCII alpha and the second
   * is either U+003A (:) or U+007C (|).
   *
   * @return
   */
  public static boolean isWindowsDriveLetter(String input, int offset) {
    if (offset + 1 < input.length()) {
      return isWindowsDriveLetter(input.codePointAt(offset), input.codePointAt(offset + 1));
    }
    return false;
  }

  /**
   * To isomorphic decode a byte sequence input, return a string whose length is equal to input’s
   * length and whose code points have the same values as input’s bytes, in the same order.
   */
  public static String isomorphicDecode(byte[] bytes, int offset, int length, Charset charset) {
    return new String(bytes, offset, length, charset);
  }

  /**
   * return the number of asciiTab and newline in the given codepoints array
   *
   * @param codepoints the array
   * @return the number of ascii tab and newline
   */
  public static int numberOfAsciiTabOrNewline(int[] codepoints) {
    int result = 0;
    for (int i = 0; i < codepoints.length; i++) {
      if (InfraHelper.isAsciiTabOrNewLine(codepoints[i])) {
        result++;
      }
    }
    return result;
  }

  /**
   * <pre>
   *   The application/x-www-form-urlencoded parser takes a byte sequence input, and then runs these steps:
   *   <ul>
   *     <li>1) Let sequences be the result of splitting input on 0x26 (&).</li>
   *     <li>2) Let output be an initially empty list of name-value tuples where both name and value hold a string.</li>
   *     <li>3) For each byte sequence bytes in sequences:
   *       <ul>
   *         <li>3.1) If bytes is the empty byte sequence, then continue.</li>
   *         <li>3.2) If bytes contains a 0x3D (=), then let name be the bytes from the start of bytes up to but excluding its first 0x3D (=), and let value be the bytes, if any, after the first 0x3D (=) up to the end of bytes. If 0x3D (=) is the first byte, then name will be the empty byte sequence. If it is the last, then value will be the empty byte sequence.</li>
   *         <li>3.3) Otherwise, let name have the value of bytes and let value be the empty byte sequence.</li>
   *         <li>3.4) Replace any 0x2B (+) in name and value with 0x20 (SP).</li>
   *         <li>3.5) Let nameString and valueString be the result of running UTF-8 decode without BOM on the percent-decoding of name and value, respectively.</li>
   *         <li>3.6) Append (nameString, valueString) to output.</li>
   *       </ul>
   *     </li>
   *     <li>4) Return output.</li>
   *   </ul>
   * </pre>
   */
  public static List<List<String>> parseFormUrlEncoded(String input) {
    Objects.requireNonNull(input);
    // 1
    List<List<String>> output = new ArrayList<>();
    // 2
    String[] sequences = input.split("&");
    for (String sequence : sequences) {
      // 3.1
      if (sequence.isEmpty()) {
        continue;
      }
      // 3.2
      String name = null;
      String value = null;
      int idxOfEqual = sequence.indexOf('=');
      if (idxOfEqual != -1) {
        name = sequence.substring(0, idxOfEqual);
        value = sequence.substring(idxOfEqual + 1);
      }
      // 3.3
      else {
        name = sequence;
        value = "";
      }
      // 3.4
      name = name.replace('=', ' ');
      value = value.replace('=', ' ');
      // 3.5
      name = EncodingHelper.utf8DecodeWithoutBom(percentDecode(name));
      value = EncodingHelper.utf8DecodeWithoutBom(percentDecode(value));
      // 3.6
      List<String> entry = new ArrayList<>();
      entry.add(name);
      entry.add(value);
      output.add(entry);
    }
    return output;
  }

  /**
   * <pre>
   * To percent-decode a byte sequence input, run these steps: <br>
   * Using anything but UTF-8 decode without BOM when input contains bytes that are not ASCII bytes
   * might be insecure and is not recommended.
   * <ul>
   *   <li>1) Let output be an empty byte sequence.</li>
   *   <li>2) For each byte byte in input:
   *     <ul>
   *       <li>2.1) If byte is not 0x25 (%), then append byte to output.</li>
   *       <li>2.2) Otherwise, if byte is 0x25 (%) and the next two bytes after byte in input are not in
   *       the ranges 0x30 (0) to 0x39 (9), 0x41 (A) to 0x46 (F), and 0x61 (a) to 0x66 (f), all inclusive,
   *       append byte to output.</li>
   *       <li>2.3) Otherwise:
   *         <ul>
   *           <li>2.3.1) Let bytePoint be the two bytes after byte in input, decoded, and then interpreted as
   *           hexadecimal number.</li>
   *           <li>2.3.2) Append a byte whose value is bytePoint to output.</li>
   *           <li>2.3.3) Skip the next two bytes in input.</li>
   *         </ul>
   *       </li>
   *     </ul>
   *   </li>
   *   <li>3) Return output.</li>
   * </ul>
   * </pre>
   */
  public static byte[] percentDecode(byte[] input) {
    // 1
    ByteArrayOutputStream output = new ByteArrayOutputStream(input.length);
    // 2
    int length = input.length;
    for (int i = 0; i < length; i++) {
      // 2.1
      if (input[i] != CodepointHelper.CP_PERCENT) {
        output.write(input[i]);
      }
      // 2.2
      else if (i + 2 < length && InfraHelper.isAsciiHexDigit(input[i + 1])
          && InfraHelper.isAsciiHexDigit(input[i + 2])) {
        String isomorphicDecode =
            InfraHelper.isomorphicDecode(new byte[] {input[i + 1], input[i + 2]});
        int bytePoint = Integer.parseInt(isomorphicDecode, 16);
        output.write(bytePoint);
        i += 2;
      }
      // 2.3
      else {
        output.write(input[i]);
      }
    }
    return output.toByteArray();
  }

  /**
   * To string percent decode a string input, run these steps: 1) Let bytes be the UTF-8 encoding of
   * input. 2) Return the percent decoding of bytes.
   */
  public static byte[] percentDecode(String input) {
    return percentDecode(input.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * To percent encode a byte into a percent-encoded byte, return a string consisting of U+0025 (%),
   * followed by two ASCII upper hex digits representing byte.
   *
   * @param abyte
   */
  public static String percentEncode(byte abyte) {
    // unsigned byte
    int i = abyte & 0xFF;
    String result = Integer.toHexString(i).toUpperCase();
    if (result.length() == 1) {
      // make sure the result is always 2 digits
      return "%0" + result;
    }
    return "%" + result;
  }

  /**
   * To percent-encode a byte byte, return a string consisting of U+0025 (%), followed by two ASCII
   * upper hex digits representing byte.
   *
   * @param abyte
   * @param result
   */
  public static void percentEncode(byte abyte, ByteArrayOutputStream result) {
    result.write('%');
    char[] hexChars = InfraHelper.toHexChars(abyte);
    result.write(hexChars[0]);
    result.write(hexChars[1]);
  }

  public static String percentEncodeAfterEncoding(CharsetEncoder encoder, int codepoint,
      IntPredicate isInEncodeSet, boolean spaceAsPlus) {
    return percentEncodeAfterEncoding(encoder, new String(Character.toChars(codepoint)),
        isInEncodeSet, spaceAsPlus);
  }

  /**
   * <pre>
   *   To percent-encode after encoding, given an encoding encoding, scalar value
   *   string input, a percentEncodeSet, and an optional boolean spaceAsPlus
   *   (default false):
   *   <ul>
   *     <li>1) Let encoder be the result of getting an encoder from encoding.</li>
   *     <li>2) Let inputQueue be input converted to an I/O queue.</li>
   *     <li>3) Let output be the empty string.</li>
   *     <li>4) Let potentialError be 0.
   *     <br>This needs to be a non-null value to initiate the subsequent while loop.
   *     </li>
   *     <li>5) While potentialError is non-null:
   *       <ul>
   *         <li>5.1) Let encodeOutput be an empty I/O queue.</li>
   *         <li>5.2) Set potentialError to the result of running encode or fail with inputQueue, encoder, and encodeOutput.</li>
   *         <li>5.3) For each byte of encodeOutput converted to a byte sequence:
   *           <ul>
   *             <li>5.3.1) If spaceAsPlus is true and byte is 0x20 (SP), then append U+002B (+) to output and continue.</li>
   *             <li>5
   *             <li>5.3.3) Assert: percentEncodeSet includes all non-ASCII code points.</li>
   *             <li>5.3.4) If isomorph is not in percentEncodeSet, then append isomorph to output.</li>
   *             <li>5.3.5) Otherwise, percent-encode byte and append the result to output.</li>
   *           </ul>
   *         </li>
   *         <li>5.4) If potentialError is non-null, then append "%26%23", followed by the shortest sequence of ASCII digits representing potentialError in base ten, followed by "%3B", to output.
   *           <br>
   *           <br>This can happen when encoding is not UTF-8.
   *         </li>
   *       </ul>
   *     </li>
   *     <li>6) Return output.</li>
   *   </ul>
   * </pre>
   */
  public static String percentEncodeAfterEncoding(CharsetEncoder encoder, CharSequence input,
      IntPredicate isInEncodeSet, boolean spaceAsPlus) {
    // early out
    if (input.length() == 0) {
      return "";
    }
    // 1
    // 2
    CharBuffer inputBuffer = CharBuffer.wrap(input);
    // 3
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    // 4
    int potentialError = 0;
    // 5
    while (potentialError >= 0) {
      // 5.1
      // 5.2
      potentialError = InfraHelper.encodeOrFail(encoder, inputBuffer, encodingResult -> {
        // 5.3
        for (int i = 0; i < encodingResult.limit(); i++) {
          byte aByte = encodingResult.get(i);
          // 5.3.1
          if (spaceAsPlus && aByte == CodepointHelper.CP_SPACE) {
            output.write(CodepointHelper.CP_PLUS);
          } else {
            // 5.3.2
            int isomorph = InfraHelper.getIsomorphInt(aByte);
            // 5.3.3
            // 5.3.4
            if (!isInEncodeSet.test(isomorph)) {
              output.write(isomorph);
            }
            // 5.3.5
            else {
              percentEncode(aByte, output);
            }
          }
        }
      });
      // 5.4
      if (potentialError >= 0) {
        output.write('%');
        output.write('2');
        output.write('6');
        output.write('%');
        output.write('2');
        output.write('3');
        String asciiError = Integer.toString(potentialError, 10);
        for (int i = 0; i < asciiError.length(); i++) {
          output.write(asciiError.charAt(i));
        }
        output.write('%');
        output.write('3');
        output.write('B');
      }
    }
    return output.toString();
  }

  public static boolean remainingMatch(String input, int pointer, int numberOfCodepointsToMatch,
      BiPredicate<Integer, Integer> predicate) {
    if (numberOfCodepointsToMatch > 0 && pointer + numberOfCodepointsToMatch < input.length()) {
      for (int i = 0; i < numberOfCodepointsToMatch; i++) {
        int codepoint = input.codePointAt(pointer + 1 + i);
        boolean result = predicate.test(i, codepoint);
        if (!result) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public static int[] removeAsciiTabAndNewline(int[] codepoints) {
    int numberOfAsciiTabOrNewline = numberOfAsciiTabOrNewline(codepoints);
    int[] result = new int[codepoints.length - numberOfAsciiTabOrNewline];
    for (int i = 0, j = 0; i < codepoints.length; i++) {
      if (!InfraHelper.isAsciiTabOrNewLine(codepoints[i])) {
        result[j++] = codepoints[i];
      }
    }
    return result;
  }

  public static int[] removeLeadingOrTrailingC0ControlOrSpace(int[] codepoints) {
    int nbrOfLeading = 0;
    for (int i = 0; i < codepoints.length; i++) {
      if (InfraHelper.isC0ControlOrSpace(codepoints[i])) {
        nbrOfLeading++;
      } else {
        break;
      }
    }
    int nbrOfTrailing = 0;
    for (int i = codepoints.length - 1; i >= nbrOfLeading; i--) {
      if (InfraHelper.isC0ControlOrSpace(codepoints[i])) {
        nbrOfTrailing++;
      } else {
        break;
      }
    }
    if (nbrOfLeading > 0 || nbrOfTrailing > 0) {
      int newLength = codepoints.length - nbrOfLeading - nbrOfTrailing;
      int[] result = new int[newLength];
      System.arraycopy(codepoints, nbrOfLeading, result, 0, newLength);
      return result;
    }
    return codepoints;
  }

  /**
   * A string starts with a Windows drive letter if all of the following are true: - its length is
   * greater than or equal to 2 - its first two code points are a Windows drive letter - its length
   * is 2 or its third code point is U+002F (/), U+005C (\), U+003F (?), or U+0023 (#).
   *
   * @return
   */
  public static boolean startsWithWindowsDriveLetter(Codepoints input) {
    Objects.requireNonNull(input);
    int remaining = input.remaining();
    int cp1 = input.codepointAt(input.pointer());
    int cp2 = input.codepointAt(input.pointer() + 1);
    int cp3 = input.codepointAt(input.pointer() + 2);
    return remaining >= 2 && UrlHelper.isWindowsDriveLetter(cp1, cp2) && (remaining == 2
        || (remaining > 2 && (cp3 == CodepointHelper.CP_SLASH || cp3 == CodepointHelper.CP_BACKSLASH
            || cp3 == CodepointHelper.CP_QUESTION_MARK || cp3 == CodepointHelper.CP_HASH)));
  }

  public static String utf8PercentEncode(CharSequence input, IntPredicate isInPercentEncodeSet) {
    return utf8PercentEncode(StandardCharsets.UTF_8.newEncoder(), input, isInPercentEncodeSet);
  }

  public static String utf8PercentEncode(CharsetEncoder utf8Encoder, CharSequence input,
      IntPredicate isInPercentEncodeSet) {
    StringBuilder result = new StringBuilder(input.length());
    for (int i = 0; i < input.length(); i++) {
      int codePoint = Character.codePointAt(input, i);
      result.append(utf8PercentEncode(utf8Encoder, codePoint, isInPercentEncodeSet));
    }
    return result.toString();
  }

  /**
   * To UTF-8 percent-encode a scalar value scalarValue using a percentEncodeSet, return the result
   * of running percent-encode after encoding with UTF-8, scalarValue as a string, and
   * percentEncodeSet.
   *
   * @param codepoint the codepoint to encode
   * @param codepoint the codepoint to encode
   * @return the percent encoded value
   */
  public static String utf8PercentEncode(CharsetEncoder utf8Encoder, int codepoint,
      IntPredicate isInPercentEncodeSet) {
    return percentEncodeAfterEncoding(utf8Encoder, codepoint, isInPercentEncodeSet, false);
  }
}
