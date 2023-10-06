package io.github.stephanebastian.whatwg.url.impl;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class HostParser {
  /**
   * <pre>
   * The ends in a number checker takes an ASCII string input and then runs
   * <br/>these steps. They return a boolean.
   * <ul>
   *   <li>1) Let parts be the result of strictly splitting input on U+002E (.).</li>
   *   <li>2) If the last item in parts is the empty string, then:
   *     <ul>
   *       <li>2.1) If parts’s size is 1, then return false.</li>
   *       <li>2.2) Remove the last item from parts.</li>
   *     </ul>
   *   </li>
   *   <li>3) Let last be the last item in parts.</li>
   *   <li>4) If last is non-empty and contains only ASCII digits, then return true.
   *   <br/>The erroneous input "09" will be caught by the IPv4 parser at a later stage.</li>
   *   <li>5) If parsing last as an IPv4 number does not return failure, then return true.
   *   <br/>This is equivalent to checking that last is "0X" or "0x",
   *   <br/>followed by zero or more ASCII hex digits.</li>
   *   <li>Return false.</li>
   * </ul>
   * </pre>
   *
   * @param input the String value to check
   * @return whether the input ends in a number
   */
  static boolean endsInANumber(String input) {
    // 1
    List<String> parts = InfraHelper.strictSplit(input, '.');
    if (parts.isEmpty()) {
      return false;
    }
    String lastPart = parts.get(parts.size() - 1);
    // 2
    if (lastPart.isEmpty()) {
      // 2.1
      if (parts.size() == 1) {
        return false;
      }
      // 2.2
      lastPart = parts.get(parts.size() - 2);
    }
    // 3
    // 4
    if (!lastPart.isEmpty() && CodepointHelper.hasOnlyAsciiDigit(lastPart)) {
      return true;
    }
    // 5
    if (lastPart.startsWith("0x") || lastPart.startsWith("0X")) {
      for (int i = 2; i < lastPart.length(); i++) {
        if (!InfraHelper.isAsciiHexDigit(lastPart.charAt(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean hasForbiddenDomainCodepoints(String domain) {
    for (int i = 0; i < domain.length(); i++) {
      if (CodepointHelper.isForbiddenDomainCodePoint(domain.codePointAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * <pre>
   *   The host parser takes a scalar value string input with an optional boolean
   *   isNotSpecial (default false), and then runs these steps. They return failure or a host.
   *   <ul>
   *     <li>1) If input starts with U+005B ([), then:
   *       <ul>
   *         <li>1.1) If input does not end with U+005D (]), IPv6-unclosed validation error,
   *         return failure.</li>
   *         <li>1.2) Return the result of IPv6 parsing input with its leading U+005B ([)
   *         and trailing U+005D (]) removed.</li>
   *       </ul>
   *     </li>
   *     <li>2) If isNotSpecial is true, then return the result of opaque-host parsing input.</li>
   *     <li>3) Assert: input is not the empty string.</li>
   *     <li>4) Let domain be the result of running UTF-8 decode without BOM on the percent-decoding of input.
   *     <br/>Note: Alternatively UTF-8 decode without BOM or fail can be used, coupled with an early return for failure, as domain to ASCII fails on U+FFFD (�).
   *     </li>
   *     <li>5) Let asciiDomain be the result of running domain to ASCII with domain and false.</li>
   *     <li>6) If asciiDomain is failure, then return failure.</li>
   *     <li>7) If asciiDomain contains a forbidden domain code point, domain-invalid-code-point validation error, return failure.</li>
   *     <li>8) If asciiDomain ends in a number, then return the result of IPv4 parsing asciiDomain.</li>
   *     <li>9) Return asciiDomain.</li>
   *   </ul>
   * </pre>
   *
   * @param input the host to parse
   * @param isNotSpecial a boolean indicating whether the host is not special
   * @param errorHandler the error handler
   * @return the Host
   */
  static Host parse(String input, boolean isNotSpecial, Consumer<UrlException> errorHandler)
      throws UrlException {
    // 1
    if (input.startsWith("[")) {
      // 1.1
      if (!input.endsWith("]")) {
        throw UrlException.IPV6_UNCLOSED;
      } else {
        return parseIpv6(input.substring(1, input.length() - 1));
      }
    }
    // 2
    if (isNotSpecial) {
      return parseOpaqueHost(input, errorHandler);
    }
    // 3
    // 4
    String domain = EncodingHelper.utf8DecodeWithoutBom(UrlHelper.percentDecode(input));
    // 5
    String asciiDomain = UrlHelper.domainToAscii(domain, false);
    // 7
    if (hasForbiddenDomainCodepoints(asciiDomain)) {
      throw UrlException.DOMAIN_INVALID_CODEPOINT;
    }
    // 8
    if (endsInANumber(asciiDomain)) {
      return parseIpv4(asciiDomain, errorHandler);
    }
    // 9
    return Domain.create(asciiDomain);
  }

  /**
   * <pre>
   * The IPv4 parser takes an ASCII string input and then runs these steps.
   * <br/>They return failure or an IPv4 address.
   * <br/>The IPv4 parser is not to be invoked directly.
   * <br/>Instead check that the return value of the host parser is an IPv4 address.
   * <ul>
   *   <li>1) Let parts be the result of strictly splitting input on U+002E (.).</li>
   *   <li>2) If the last item in parts is the empty string, then:
   *     <ul>
   *       <li>2.1) IPv4-empty-part validation error.</li>
   *       <li>2.2) If parts’s size is greater than 1, then remove the last item from parts.</li>
   *     </ul>
   *   </li>
   *   <li>3) If parts’s size is greater than 4, IPv4-too-many-parts validation error,
   *   return failure.</li>
   *   <li>4) Let numbers be an empty list.</li>
   *   <li>5) For each part of parts:
   *     <ul>
   *       <li>5.1) Let result be the result of parsing part.</li>
   *       <li>5.2) If result is failure, IPv4-non-numeric-part validation error, return failure.</li>
   *       <li>5.3) If result[1] is true, IPv4-non-decimal-part validation error.</li>
   *       <li>5.4) Append result[0] to numbers.</li>
   *     </ul>
   *   </li>
   *   <li>6) If any item in numbers is greater than 255, IPv4-out-of-range-part validation error.</li>
   *   <li>7) If any but the last item in numbers is greater than 255, then return failure.</li>
   *   <li>8) If the last item in numbers is greater than or equal to 256(5 − numbers’s size),
   *   then return failure.</li>
   *   <li>9) Let ipv4 be the last item in numbers.</li>
   *   <li>10) Remove the last item from numbers.</li>
   *   <li>11) Let counter be 0.</li>
   *   <li>12) For each n of numbers:
   *     <ul>
   *       <li>12.1) Increment ipv4 by n × 256(3 − counter).</li>
   *       <li>12.2) Increment counter by 1.</li>
   *     </ul>
   *   </li>
   *   <li>13) Return ipv4.</li>
   * </ul>
   * </pre>
   *
   * @param input the input to parse
   * @param errorHandler the error handler
   * @return the {@link Ipv4Address}
   */
  static Ipv4Address parseIpv4(String input, Consumer<UrlException> errorHandler)
      throws UrlException {
    // 1
    List<String> parts = InfraHelper.strictSplit(input, '.');
    // 2
    if (parts.isEmpty() || parts.get(parts.size() - 1).isEmpty()) {
      // 2.1
      errorHandler.accept(UrlException.IPV4_EMPTY_PART);
      // 2.2
      if (parts.size() > 1) {
        parts.remove(parts.size() - 1);
      }
    }
    // 3
    if (parts.size() > 4) {
      throw UrlException.IPV4_TOO_MANY_PARTS;
    }
    // 4
    int[] numbers = new int[parts.size()];
    // 5
    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);
      try {
        // 5.1
        Map.Entry<Integer, Boolean> parsedNumber = parseIpv4Number(part);
        // 5.3
        if (parsedNumber.getValue()) {
          errorHandler.accept(UrlException.IPV4_NON_DECIMAL_PART);
        }
        // 5.4
        numbers[i] = parsedNumber.getKey();
      } catch (Exception e) {
        // 5.2
        throw UrlException.IPV4_NON_NUMERIC_PART;
      }
    }
    // 6
    for (int number : numbers) {
      if (number > 255) {
        errorHandler.accept(UrlException.IPV4_OUT_OF_RANGE_PART);
        break;
      }
    }
    // 7
    for (int i = 0; i < numbers.length - 1; i++) {
      if (numbers[i] > 255 && i < numbers.length - 1) {
        throw UrlException.IPV4_OUT_OF_RANGE_PART;
      }
    }
    // 8
    if (numbers[numbers.length - 1] >= Math.pow(256, (5 - numbers.length))) {
      throw UrlException.IPV4_OUT_OF_RANGE_PART;
    }
    // 9
    int ipv4 = numbers[numbers.length - 1];
    // 10, 11, 12
    for (int i = 0, counter = 0; i < numbers.length - 1; i++, counter++) {
      // 12.1
      int n = numbers[i];
      ipv4 = ipv4 + n * (int) Math.pow(256, (3 - counter));
    }
    // 14
    return Ipv4Address.create(ipv4);
  }

  /**
   * <pre>
   * The IPv4 number parser takes an ASCII string input and then runs these steps.
   * <br/>They return failure or a tuple of a number and a boolean.
   * <ul>
   *   <li>1) If input is the empty string, then return failure.</li>
   *   <li>2) Let validationError be false.</li>
   *   <li>3) Let R be 10.</li>
   *   <li>4) If input contains at least two code points and the first two code points
   *   <br/>are either "0X" or "0x", then:
   *     <ul>
   *        <li>4.1) Set validationError to true.</li>
   *        <li>4.2) Remove the first two code points from input.</li>
   *        <li>4.3) Set R to 16.</li>
   *     </ul>
   *   </li>
   *   <li>5) Otherwise, if input contains at least two code points
   *   <br/>and the first code point is U+0030 (0), then:
   *     <ul>
   *       <li>5.1) Set validationError to true.</li>
   *       <li>5.2) Remove the first code point from input.</li>
   *       <li>5.3) Set R to 8.</li>
   *     </ul>
   *   </li>
   *   <li>6) If input is the empty string, then return (0, true).</li>
   *   <li>7) If input contains a code point that is not a radix-R digit, then return failure.</li>
   *   <li>8) Let output be the mathematical integer value that is represented
   *   <br/>by input in radix-R notation, using ASCII hex digits for digits with values 0 through 15.</li>
   *   <li>9) Return (output, validationError).</li>
   * </ul>
   * </pre>
   *
   * @param input the input to parse
   * @return a tuple whose key is the parsed number and the value is a boolean indicating whether a
   *         validation error occurred
   */
  static Map.Entry<Integer, Boolean> parseIpv4Number(String input) throws UrlException {
    Objects.requireNonNull(input);
    // 1
    if (input.isEmpty()) {
      throw UrlException._IPV4_NUMBER_PARSER;
    }
    // 2
    boolean validationError = false;
    // 3
    int radix = 10;
    // 4
    if (input.startsWith("0x") || input.startsWith("0X")) {
      // 4.1
      validationError = true;
      // 4.2
      input = input.substring(2);
      // 4.3
      radix = 16;
    }
    // 5
    else if (input.length() >= 2 && input.startsWith("0")) {
      // 5.1
      validationError = true;
      // 5.2
      input = input.substring(1);
      // 5.3
      radix = 8;
    }
    // 6
    if (input.isEmpty()) {
      return new AbstractMap.SimpleEntry<Integer, Boolean>(0, true);
    }
    // 7
    for (int i = 0; i < input.length(); i++) {
      if (Character.digit(input.codePointAt(i), radix) == -1) {
        throw UrlException._IPV4_NUMBER_PARSER;
      }
    }
    try {
      // 8
      int output = Integer.parseUnsignedInt(input, radix);
      // 9
      return new AbstractMap.SimpleEntry<Integer, Boolean>(output, validationError);
    } catch (Throwable t) {
      throw UrlException._IPV4_NUMBER_PARSER;
    }
  }

  /**
   * <pre>
   * The IPv6 parser takes a scalar value string input and then runs these steps.
   * <br/>They return failure or an IPv6 address.
   * <br/>The IPv6 parser could in theory be invoked directly, but please discuss
   * <br/>actually doing that with the editors of this document first.
   * <ul>
   *   <li>1) Let address be a new IPv6 address whose IPv6 pieces are all 0.</li>
   *   <li>2) Let pieceIndex be 0.</li>
   *   <li>3) Let compress be null.</li>
   *   <li>4) Let pointer be a pointer for input.</li>
   *   <li>5) If c is U+003A (:), then:
   *     <ul>
   *       <li>5.1) If remaining does not start with U+003A (:),
   *       IPv6-invalid-compression validation error, return failure.</li>
   *       <li>5.2) Increase pointer by 2.</li>
   *       <li>5.3) Increase pieceIndex by 1 and then set compress to pieceIndex.</li>
   *     </ul>
   *   </li>
   *   <li>6) While c is not the EOF code point:
   *     <ul>
   *       <li>6.1) If pieceIndex is 8, IPv6-too-many-pieces validation error, return failure.</li>
   *       <li>6.2) If c is U+003A (:), then:
   *         <ul>
   *           <li>6.2.1) If compress is non-null, IPv6-multiple-compression validation error,
   *           return failure.</li>
   *           <li>6.2.2) Increase pointer and pieceIndex by 1, set compress to pieceIndex,
   *           and then continue.</li>
   *         </ul>
   *       </li>
   *       <li>6.3) Let value and length be 0.</li>
   *       <li>6.4) While length is less than 4 and c is an ASCII hex digit,
   *       set value to value × 0x10 + c interpreted as hexadecimal number,
   *       and increase pointer and length by 1.</li>
   *       <li>6.5) If c is U+002E (.), then:
   *         <ul>
   *           <li>6.5.1) If length is 0, IPv4-in-IPv6-invalid-code-point validation error,
   *           return failure.</li>
   *           <li>6.5.2) Decrease pointer by length.</li>
   *           <li>6.5.3) If pieceIndex is greater than 6, IPv4-in-IPv6-too-many-pieces
   *           validation error, return failure.</li>
   *           <li>6.5.4) Let numbersSeen be 0.</li>
   *           <li>6.5.5) While c is not the EOF code point:
   *             <ul>
   *               <li>6.5.5.1) Let ipv4Piece be null.</li>
   *               <li>6.5.5.2) If numbersSeen is greater than 0, then:
   *                 <ul>
   *                   <li>6.5.5.2.1) If c is a U+002E (.) and numbersSeen is less than 4,
   *                   then increase pointer by 1.</li>
   *                   <li>Otherwise, IPv4-in-IPv6-invalid-code-point validation error,
   *                   return failure.</li>
   *                 </ul>
   *               </li>
   *               <li>6.5.5.3) If c is not an ASCII digit, IPv4-in-IPv6-invalid-code-point validation error,
   *               return failure.</li>
   *               <li>6.5.5.4) While c is an ASCII digit:
   *                 <ul>
   *                   <li>6.5.5.4.1) Let number be c interpreted as decimal number.</li>
   *                   <li>6.5.5.4.2) If ipv4Piece is null, then set ipv4Piece to number.
   *                   <br/>Otherwise, if ipv4Piece is 0, IPv4-in-IPv6-invalid-code-point validation error,
   *                   <br/>return failure.
   *                   <br/>
   *                   <br/>Otherwise, set ipv4Piece to ipv4Piece × 10 + number.
   *                   </li>
   *                   <li>6.5.5.4.3) If ipv4Piece is greater than 255, IPv4-in-IPv6-out-of-range-part validation error,
   *                   return failure.</li>
   *                   <li>6.5.5.4.4) Increase pointer by 1.</li>
   *                 </ul>
   *               </li>
   *               <li>6.5.5.5) Set address[pieceIndex] to address[pieceIndex] × 0x100 + ipv4Piece.</li>
   *               <li>6.5.5.6) Increase numbersSeen by 1.</li>
   *               <li>6.5.5.7) If numbersSeen is 2 or 4, then increase pieceIndex by 1.</li>
   *             </ul>
   *           </li>
   *           <li>6.5.6) If numbersSeen is not 4, IPv4-in-IPv6-too-few-parts validation error,
   *           return failure.</li>
   *           <li>6.5.7) Break.</li>
   *         </ul>
   *       </li>
   *       <li>6.6) Otherwise, if c is U+003A (:):
   *         <ul>
   *           <li>6.6.1) Increase pointer by 1.</li>
   *           <li>6.6.2) If c is the EOF code point, IPv6-invalid-code-point validation error,
   *           return failure.</li>
   *         </ul>
   *       </li>
   *       <li>6.7) Otherwise, if c is not the EOF code point, IPv6-invalid-code-point validation error,
   *       return failure.</li>
   *       <li>6.8) Set address[pieceIndex] to value.</li>
   *       <li>6.9) Increase pieceIndex by 1.</li>
   *     </ul>
   *   </li>
   *   <li>7) If compress is non-null, then:
   *     <ul>
   *       <li>7.1) Let swaps be pieceIndex − compress.</li>
   *       <li>7.2) Set pieceIndex to 7.</li>
   *       <li>7.3) While pieceIndex is not 0 and swaps is greater than 0, swap address[pieceIndex]
   *       with address[compress + swaps − 1], and then decrease both pieceIndex and swaps by 1.</li>
   *     </ul>
   *   </li>
   *   <li>8) Otherwise, if compress is null and pieceIndex is not 8, IPv6-too-few-pieces validation error,
   *   return failure.</li>
   *   <li>9) Return address.</li>
   * </ul>
   * </pre>
   *
   * @param input the string value to parse as an ipv6 address
   * @return
   */
  static Ipv6Address parseIpv6(String input) throws UrlException {
    // 1
    short[] address = new short[8];
    // 2
    int pieceIndex = 0;
    // 3
    Integer compress = null;
    // 4
    int pointer = 0;
    // 5
    if (UrlHelper.codePoint(input, pointer) == 0x003A/* : */) {
      // 5.1
      if (!UrlHelper.remainingMatch(input, pointer, 1,
          (idx, cp) -> cp == CodepointHelper.CP_COLON)) {
        throw UrlException.IPV6_INVALID_COMPRESSION;
      }
      // 5.2
      pointer += 2;
      // 5.3
      pieceIndex++;
      compress = pieceIndex;
    }
    // 6
    while ((UrlHelper.codePoint(input, pointer)) != CodepointHelper.CP_EOF) {
      // 6.1
      if (pieceIndex == 8) {
        throw UrlException.IPV6_TOO_MANY_PIECES;
      }
      // 6.2
      if (UrlHelper.codePoint(input, pointer) == CodepointHelper.CP_COLON) {
        // 6.2.1
        if (compress != null) {
          throw UrlException.IPV6_MULTIPLE_COMPRESSION;
        }
        // 6.2.2
        pointer++;
        pieceIndex++;
        compress = pieceIndex;
        continue;
      }
      // 6.3
      int value = 0;
      int length = 0;
      // 6.4
      while (length < 4 && InfraHelper.isAsciiHexDigit(UrlHelper.codePoint(input, pointer))) {
        value = value * 0x10 + Character.digit(UrlHelper.codePoint(input, pointer), 16);
        pointer++;
        length++;
      }
      // 6.5
      if (UrlHelper.codePoint(input, pointer) == CodepointHelper.CP_PERIOD) {
        // 6.5.1
        if (length == 0) {
          throw UrlException.IPV6_INVALID_CODEPOINT;
        }
        // 6.5.2
        pointer = pointer - length;
        // 6.5.3
        if (pieceIndex > 6) {
          throw UrlException.IPV6_TOO_MANY_PIECES;
        }
        // 6.5.4
        int numberSeen = 0;
        // 6.5.5
        while (UrlHelper.codePoint(input, pointer) != CodepointHelper.CP_EOF) {
          // 6.5.5.1
          Short ipv4Piece = null;
          // 6.5.5.2
          if (numberSeen > 0) {
            if (UrlHelper.codePoint(input, pointer) == CodepointHelper.CP_PERIOD
                && numberSeen < 4) {
              // 6.5.5.2.1
              pointer++;
            } else {
              throw UrlException.IPV6_INVALID_CODEPOINT;
            }
          }
          // 6.5.5.3
          if (!InfraHelper.isAsciiDigit(UrlHelper.codePoint(input, pointer))) {
            throw UrlException.IPV6_INVALID_CODEPOINT;
          }
          // 6.5.5.4
          while (InfraHelper.isAsciiDigit(UrlHelper.codePoint(input, pointer))) {
            // 6.5.5.4.1
            short number = (short) Character.digit(UrlHelper.codePoint(input, pointer), 10);
            // 6.5.5.4.2
            if (ipv4Piece == null) {
              ipv4Piece = number;
            } else if (ipv4Piece == 0) {
              throw UrlException.IPV6_INVALID_CODEPOINT;
            } else {
              ipv4Piece = (short) (ipv4Piece * 10 + number);
            }
            // 6.5.5.4.3
            if (ipv4Piece > 255) {
              throw UrlException.IPV4_OUT_OF_RANGE_PART;
            }
            // 6.5.5.4.4
            pointer++;
          }
          // 6.5.5.5
          address[pieceIndex] = (short) (address[pieceIndex] * 0x100 + ipv4Piece);
          // 6.5.5.6
          numberSeen++;
          // 6.5.5.7
          if (numberSeen == 2 || numberSeen == 4) {
            pieceIndex++;
          }
        }
        // 6.5.6
        if (numberSeen != 4) {
          throw UrlException.IPV4_IN_IPV6_TOO_FEW_PARTS;
        }
        // 6.5.7
        break;
      }
      // 6.6
      else if (UrlHelper.codePoint(input, pointer) == CodepointHelper.CP_COLON) {
        // 6.6.1
        pointer++;
        // 6.6.2
        if (UrlHelper.codePoint(input, pointer) == CodepointHelper.CP_EOF) {
          throw UrlException.IPV6_INVALID_CODEPOINT;
        }
      }
      // 6.7
      else if (UrlHelper.codePoint(input, pointer) != CodepointHelper.CP_EOF) {
        throw UrlException.IPV6_INVALID_CODEPOINT;
      }
      // 6.8
      address[pieceIndex] = (short) value;
      // 6.9
      pieceIndex++;
    }
    // 7
    if (compress != null) {
      // 7.1
      int swaps = pieceIndex - compress;
      // 7.2
      pieceIndex = 7;
      // 7.3
      while (pieceIndex > 0 && swaps > 0) {
        int tmp1 = address[pieceIndex];
        int tmp2 = address[compress + swaps - 1];
        address[pieceIndex] = (short) tmp2;
        address[compress + swaps - 1] = (short) tmp1;
        pieceIndex--;
        swaps--;
      }
    }
    // 8
    else if (compress == null && pieceIndex != 8) {
      throw UrlException.IPV6_TOO_FEW_PIECES;
    }
    return Ipv6Address.create(address);
  }

  /**
   * <pre>
   * The opaque-host parser takes a scalar value string input, and then runs these steps.
   * <br/>They return failure or an opaque host.
   * <ul>
   *   <li>1) If input contains a forbidden host code point, host-invalid-code-point
   *   validation error, return failure.</li>
   *   <li>2) If input contains a code point that is not a URL code point and not U+0025 (%),
   *   invalid-URL-unit validation error.</li>
   *   <li>3) If input contains a U+0025 (%) and the two code points following it
   *   are not ASCII hex digits, invalid-URL-unit validation error.</li>
   *   <li>4) Return the result of running UTF-8 percent-encode on input using
   *   the C0 control percent-encode set.</li>
   * </ul>
   * </pre>
   */
  static Host parseOpaqueHost(String input, Consumer<UrlException> errorHandler)
      throws UrlException {
    for (int i = 0; i < input.length(); i++) {
      int codePoint = input.codePointAt(i);
      // 1
      if (CodepointHelper.isForbiddenHostCodePoint(codePoint)) {
        throw UrlException.HOST_INVALID_CODEPOINT;
      }
      // 2
      if (!CodepointHelper.isUrlCodepoint(codePoint) && codePoint != CodepointHelper.CP_PERCENT) {
        errorHandler.accept(UrlException.INVALID_URL_UNIT);
      }
      // 3
      if (codePoint == CodepointHelper.CP_PERCENT
          && !InfraHelper.isAsciiHexDigit(UrlHelper.codePoint(input, i + 1))
          && !InfraHelper.isAsciiHexDigit(UrlHelper.codePoint(input, i + 2))) {
        errorHandler.accept(UrlException.INVALID_URL_UNIT);
      }
    }
    // 4
    return OpaqueHost
        .create(UrlHelper.utf8PercentEncode(input, CodepointHelper::isInC0ControlPercentEncodeSet));
  }
}
