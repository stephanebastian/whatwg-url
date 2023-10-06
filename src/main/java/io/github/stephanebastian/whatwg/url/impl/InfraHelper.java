package io.github.stephanebastian.whatwg.url.impl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class InfraHelper {
  /**
   * <pre>
   *   To collect a sequence of code points meeting a condition condition from a
   *   string input, given a position variable position tracking the position of
   *   the calling algorithm within input:
   *   <ul>
   *     <li>1) Let result be the empty string.</li>
   *     <li>2) While position doesn’t point past the end of input and the code point
   *     at position within input meets the condition condition:
   *       <ul>
   *         <li>2.1) Append that code point to the end of result.</li>
   *         <li>2.2) Advance position by 1.</li>
   *       </ul>
   *     </li>
   *     <li>3) Return result.</li>
   *   </ul>
   * </pre>
   *
   * @param input
   * @param position
   * @param condition
   * @return
   */
  public static String collectCodePoints(String input, int position, IntPredicate condition) {
    // 1
    StringBuilder result = new StringBuilder();
    if (input != null) {
      // 2
      while (position < input.length() && condition.test(input.charAt(position))) {
        // 2.1
        result.append(input.charAt(position));
        // 2.2
        position++;
      }
    }
    // 3
    return result.toString();
  }

  static int doDecode(CharsetDecoder decoder, ByteBuffer inputBuffer, ErrorMode errorMode,
      Consumer<CharBuffer> resultHandler) {
    Objects.requireNonNull(decoder);
    Objects.requireNonNull(inputBuffer);
    Objects.requireNonNull(errorMode);
    Objects.requireNonNull(resultHandler);
    if (inputBuffer.remaining() == 0) {
      // nothing to encode
      return -1;
    }
    // make sure outputBuffer is 'big enough' to do the whole thing in one shot
    CharBuffer outputBuffer = CharBuffer.allocate(inputBuffer.capacity() * 5);
    CoderResult coderResult = null;
    while (inputBuffer.remaining() > 0) {
      outputBuffer.clear();
      coderResult = decoder.decode(inputBuffer, outputBuffer, true);
      outputBuffer.flip();
      // notify the caller
      resultHandler.accept(outputBuffer);
      // handle errors
      if (coderResult.isError()) {
        outputBuffer.clear();
        byte byteInError = inputBuffer.get(inputBuffer.position());
        // skip the codepoint in error so that the next encode won't loop over the same error
        if (inputBuffer.position() < inputBuffer.limit() + 1) {
          inputBuffer.position(inputBuffer.position() + 1);
        }
        if (errorMode == ErrorMode.REPLACEMENT) {
          outputBuffer.put('\uFFFD');
          outputBuffer.flip();
          resultHandler.accept(outputBuffer);
        } else if (errorMode == ErrorMode.HTML) {
          outputBuffer.put('&');
          outputBuffer.put('#');
          Integer.toString((int) byteInError, 10).chars()
              .forEach(achar -> outputBuffer.put((char) achar));
          // result’s code point’s value in base ten
          outputBuffer.put(';');
          outputBuffer.flip();
          resultHandler.accept(outputBuffer);
        } else if (errorMode == ErrorMode.FATAL) {
          return byteInError;
        }
      }
    }
    return -1;
  }

  /**
   * <pre>
   *   doEncode is equivalent to processQueue/ProcessItem whose algorithm are described below.
   *   It does its job and call the resultHandler whenever
   *
   *   <br/>
   *   </br/>ProcessQueue:
   *   To encode an I/O queue of scalar values ioQueue given an encoding encoding
   *   and an optional I/O queue of bytes output (default « »), run these steps:
   *   <ul>
   *     <li>1) Let encoder be the result of getting an encoder from encoding.</li>
   *     <li>2) Process a queue with encoder, ioQueue, output, and "html".</li>
   *     <li>3) Return output.</li>
   *   </ul>
   *
   *   <br/>
   *   </br/>ProcessItem:
   *   To process an item given an item item, encoding’s encoder or decoder
   *   instance encoderDecoder, I/O queue input, I/O queue output, and error mode mode:
   *   <ul>
   *     <li>1) Assert: if encoderDecoder is an encoder instance, mode is not "replacement".</li>
   *     <li>2) Assert: if encoderDecoder is a decoder instance, mode is not "html".</li>
   *     <li>3) Assert: if encoderDecoder is an encoder instance, item is not a surrogate.</li>
   *     <li>4) Let result be the result of running encoderDecoder’s handler on input and item.</li>
   *     <li>5) If result is finished:
   *       <ul>
   *         <li>5.1) Push end-of-queue to output.</li>
   *         <li>5.2) Return result.</li>
   *       </ul>
   *     </li>
   *     <li>6) Otherwise, if result is one or more items:
   *       <ul>
   *         <li>6.1) Assert: if encoderDecoder is a decoder instance, result does not contain any surrogates.</li>
   *         <li>6.2) Push result to output.</li>
   *       </ul>
   *     </li>
   *     <li>7) Otherwise, if result is an error, switch on mode and run the associated steps:
   *       <ul>
   *         <li>"replacement": Push U+FFFD (�) to output.</li>
   *         <li>"html": Push 0x26 (&), 0x23 (#), followed by the shortest sequence of 0x30 (0) to 0x39 (9), inclusive, representing result’s code point’s value in base ten, followed by 0x3B (;) to output.</li>
   *         <li>"fatal": Return result.</li>
   *       </ul>
   *     </li>
   *     <li>8) Return continue.</li>
   *   </ul>
   * </pre>
   */
  static int doEncode(CharsetEncoder encoder, CharBuffer inputBuffer, ErrorMode errorMode,
      Consumer<ByteBuffer> resultHandler) {
    Objects.requireNonNull(encoder);
    Objects.requireNonNull(inputBuffer);
    Objects.requireNonNull(errorMode);
    Objects.requireNonNull(resultHandler);
    if (inputBuffer.remaining() == 0) {
      // nothing to encode
      return -1;
    }
    // make sure outputBuffer is 'big enough'
    int outputBufferLength = (int) (inputBuffer.remaining() * encoder.maxBytesPerChar());
    ByteBuffer outputBuffer = ByteBuffer.allocate(outputBufferLength);
    encoder.reset();
    CoderResult coderResult = CoderResult.OVERFLOW;
    while (coderResult != CoderResult.UNDERFLOW) {
      outputBuffer.clear();
      coderResult = inputBuffer.hasRemaining() ? encoder.encode(inputBuffer, outputBuffer, true)
          : CoderResult.UNDERFLOW;
      // make sure we call flush because some encoder (iso-200-jp for instance) write stuff at the
      // end
      if (coderResult.isUnderflow()) {
        coderResult = encoder.flush(outputBuffer);
      }
      outputBuffer.flip();
      // notify the caller
      resultHandler.accept(outputBuffer);
      // handle errors
      if (coderResult.isError()) {
        // clear the output buffer to report errors if applicable
        outputBuffer.clear();
        char charInError = inputBuffer.get(inputBuffer.position());
        // skip the codepoint in error so that the next encode won't loop over the same error
        if (inputBuffer.position() < inputBuffer.limit() + 1) {
          inputBuffer.position(inputBuffer.position() + 1);
        }
        if (errorMode == ErrorMode.REPLACEMENT) {
          outputBuffer.putChar('\uFFFD');
          outputBuffer.flip();
          resultHandler.accept(outputBuffer);
        } else if (errorMode == ErrorMode.HTML) {
          outputBuffer.putChar('&');
          outputBuffer.putChar('#');
          Integer.toString((int) charInError, 10).chars().forEach(outputBuffer::putInt);
          // result’s code point’s value in base ten
          outputBuffer.putChar(';');
          outputBuffer.flip();
          resultHandler.accept(outputBuffer);
        } else if (errorMode == ErrorMode.FATAL) {
          return charInError;
        }
      }
    }
    return -1;
  }

  /**
   * <pre>
   *   To encode or fail an I/O queue of scalar values ioQueue given an encoder
   *   instance encoder and an I/O queue of bytes output, run these steps:</li>
   *   <ul>
   *     <li>1) Let potentialError be the result of processing a queue with encoder,
   *     ioQueue, output, and "fatal".</li>
   *     <li>2) Push end-of-queue to output.</li>
   *     <li>3) If potentialError is an error, then return error’s code point’s value.</li>
   *     <li>4) Return null.</li>
   *   </ul>
   * </pre>
   *
   * @param encoder the encoder to use
   * @param inputBuffer the input to encode
   * @param resultHandler the result handler
   */
  public static int encodeOrFail(CharsetEncoder encoder, CharBuffer inputBuffer,
      Consumer<ByteBuffer> resultHandler) {
    // doEncode may call the callback several times, unless an error occurred in which case it stops
    // the encoding process
    return doEncode(encoder, inputBuffer, ErrorMode.FATAL, resultHandler);
  }

  static byte[] getBytes(int value) {
    return new byte[] {(byte) (value >> 24 & 0x00FF), (byte) (value >> 16 & 0x00FF),
        (byte) (value >> 8 & 0x00FF), (byte) (value & 0x00FF)};
  }

  /**
   * the isomorph value of a byte is a code point whose value is byte’s value.
   *
   * @param aByte
   * @return
   */
  public static int getIsomorphInt(byte aByte) {
    return Byte.toUnsignedInt(aByte);
  }

  /**
   * To get an output encoding from an encoding encoding, run these steps: If encoding is
   * replacement, UTF-16BE, or UTF-16LE, return UTF-8. Return encoding.
   *
   * @param encoding
   * @return
   */
  public static Charset getOutputEncoding(Charset encoding) {
    if (encoding == StandardCharsets.UTF_16BE || encoding == StandardCharsets.UTF_16LE) {
      return StandardCharsets.UTF_8;
    } else {
      return encoding;
    }
  }

  // An ASCII alpha is an ASCII upper alpha or ASCII lower alpha.
  public static boolean isAsciiAlpha(int codepoint) {
    return isAsciiUpperAlpha(codepoint) || isAsciiLowerAlpha(codepoint);
  }

  // An ASCII alphanumeric is an ASCII digit or ASCII alpha.
  public static boolean isAsciiAlphanumeric(int codepoint) {
    return isAsciiDigit(codepoint) || isAsciiAlpha(codepoint);
  }

  // An ASCII digit is a code point in the range U+0030 (0) to U+0039 (9),
  // inclusive.
  public static boolean isAsciiDigit(int codepoint) {
    return codepoint >= 0x0030 && codepoint <= 0x0039;
  }

  // An ASCII hex digit is an ASCII upper hex digit or ASCII lower hex digit.
  public static boolean isAsciiHexDigit(int codepoint) {
    return isAsciiUpperHexDigit(codepoint) || isAsciiLowerHexDigit(codepoint);
  }

  // An ASCII lower alpha is a code point in the range U+0061 (a) to U+007A (z),
  // inclusive.
  public static boolean isAsciiLowerAlpha(int codepoint) {
    return codepoint >= 0x0061 && codepoint <= 0x007A;
  }

  // An ASCII lower hex digit is an ASCII digit or a code point in the range
  // U+0061 (a) to U+0066
  // (f), inclusive.
  public static boolean isAsciiLowerHexDigit(int codepoint) {
    return isAsciiDigit(codepoint) || codepoint >= 0x0061 && codepoint <= 0x0066;
  }

  // An ASCII tab
  public static boolean isAsciiTabOrNewLine(int codepoint) {
    return codepoint == 0x009 || codepoint == 0x00A || codepoint == 0x00D;
  }

  // An ASCII upper alpha is a code point in the range U+0041 (A) to U+005A (Z),
  // inclusive.
  public static boolean isAsciiUpperAlpha(int codepoint) {
    return codepoint >= 0x0041 && codepoint <= 0x005A;
  }

  // An ASCII upper hex digit is an ASCII digit or a code point in the range
  // U+0041 (A) to U+0046
  // (F), inclusive.
  public static boolean isAsciiUpperHexDigit(int codepoint) {
    return isAsciiDigit(codepoint) || codepoint >= 0x0041 && codepoint <= 0x0046;
  }

  /**
   * A C0 control is a code point in the range U+0000 NULL to U+001F INFORMATION SEPARATOR ONE,
   * inclusive.
   *
   * @param codepoint
   * @return
   */
  static boolean isC0Control(int codepoint) {
    return codepoint >= 0x0000 && codepoint <= 0x001F;
  }

  public static boolean isC0ControlOrSpace(int codepoint) {
    return isC0Control(codepoint) || codepoint == 0x0020;
  }

  // A control is a C0 control or a code point in the range U+007F DELETE to
  // U+009F APPLICATION
  // PROGRAM COMMAND, inclusive.
  public static boolean isControl(int codepoint) {
    return isC0Control(codepoint) || (codepoint >= 0x007F && codepoint <= 0x009F);
  }

  public static boolean isScalarValue(int codepoint) {
    return !isSurrogate(codepoint);
  }

  /**
   * A surrogate is a code point that is in the range U+D800 to U+DFFF, inclusive.
   *
   * @return
   */
  public static boolean isSurrogate(int codePoint) {
    return codePoint >= 0xD800 && codePoint <= 0xDFFF;
  }

  /**
   * To isomorphic decode a byte sequence input, return a string whose code point length is equal to
   * input’s length and whose code points have the same values as the values of input’s bytes, in
   * the same order.
   */
  public static String isomorphicDecode(byte[] bytes) {
    int[] codepoints = new int[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      codepoints[i] = (int) bytes[i];
    }
    return new String(codepoints, 0, codepoints.length);
  }

  /**
   * <pre>
   *   To strictly split a string input on a particular delimiter code point delimiter:
   *   <ul>
   *     <li>1) Let position be a position variable for input, initially
   *     pointing at the start of input.</li>
   *     <li>2) Let tokens be a list of strings, initially empty.</li>
   *     <li>3) Let token be the result of collecting a sequence of code points
   *     that are not equal to delimiter from input, given position.</li>
   *     <li>4) Append token to tokens.</li>
   *     <li>5) While position is not past the end of input:
   *       <ul>
   *         <li>5.1) Assert: the code point at position within input is delimiter.</li>
   *         <li>5.2) Advance position by 1.</li>
   *         <li>5.3) Let token be the result of collecting a sequence of code points
   *         that are not equal to delimiter from input, given position.</li>
   *         <li>5.4) Append token to tokens.</li>
   *       </ul>
   *       <li>6) Return tokens.</li>
   *     </li>
   *   </ul>
   * </pre>
   *
   * @param value
   * @param delimiter
   * @return
   */
  public static List<String> strictSplit(String value, char delimiter) {
    // 1
    int position = 0;
    // 2
    List<String> tokens = new ArrayList<>();
    // 3
    String token = collectCodePoints(value, position, codepoint -> delimiter != codepoint);
    // 4
    tokens.add(token);
    position += token.length();
    // 5
    while (position < value.length()) {
      // 5.2
      position++;
      // 5.3
      token = collectCodePoints(value, position, codepoint -> delimiter != codepoint);
      // 5.4
      tokens.add(token);
      position += token.length();
    }
    // 6
    return tokens;
  }

  /**
   * convert the specified byte value (unsigned) to its hexadecimal representation as two ASCII
   * upper hex digit (0 to 9, A to F)
   *
   * @param value
   * @return
   */
  static char[] toHexChars(byte value) {
    byte unsignedValue = (byte) (value & 0xF);
    return new char[] {Character.toUpperCase(Character.forDigit((value >> 4) & 0xf, 16)),
        Character.toUpperCase(Character.forDigit(value & 0xf, 16))};
  }

  /**
   * A scalar value string is a string whose code points are all scalar values. <br/>
   * <br/>
   * A scalar value string is useful for any kind of I/O or other kind <br/>
   * of operation where UTF-8 encode comes into play. <br/>
   * <br/>
   * To convert a string into a scalar value string, replace any surrogates with U+FFFD (�).
   *
   * @param codepoints the codepoints array whose surrogates need to be replaced
   * @return the input array is returned with surrogates replaced with '\uFFFD'
   */
  public static int[] toScalarCodepoints(int[] codepoints) {
    Objects.requireNonNull(codepoints);
    for (int i = 0; i < codepoints.length; i++) {
      int codepoint = codepoints[i];
      if (!isScalarValue(codepoint)) {
        // replace the surrogate
        codepoints[i] = '\uFFFD';
      }
    }
    return codepoints;
  }

  public enum ErrorMode {
    REPLACEMENT, FATAL, HTML
  }
}
