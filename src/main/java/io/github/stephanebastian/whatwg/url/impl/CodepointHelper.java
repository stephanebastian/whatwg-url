package io.github.stephanebastian.whatwg.url.impl;

public class CodepointHelper {
  protected final static int CP_BOF = -2; // NOT IN THE SPEC BUT ADDED FOR CONVENIENCE
  protected final static int CP_EOF = -1;
  protected final static int CP_SPACE = 0x0020; //
  protected final static int CP_QUOTATION_MARK = 0x0022; // "
  protected final static int CP_HASH = 0x0023; // #
  protected final static int CP_PERCENT = 0x0025; // %
  protected final static int CP_APOSTROPHE = 0x0027; // '
  protected final static int CP_PLUS = 0x002B; // +
  protected final static int CP_MINUS = 0x002D; // -
  protected final static int CP_PERIOD = 0x002E; // .
  protected final static int CP_SLASH = 0x002F; // /
  protected final static int CP_COLON = 0x003A; // :
  protected final static int CP_LESS_THAN = 0x003C; // <
  protected final static int CP_GREATER_THAN = 0x003E; // >
  protected final static int CP_QUESTION_MARK = 0x003F; // ?
  protected final static int CP_AT = 0x0040; // @
  protected final static int CP_LEFT_SQUARE_BRACKET = 0x005B; // [
  protected final static int CP_BACKSLASH = 0x005C; // \
  protected final static int CP_RIGHT_SQUARE_BRACKET = 0x005D; // ]

  public static boolean hasOnlyAsciiDigit(String value) {
    if (value != null && !value.isEmpty()) {
      for (int i = 0; i < value.length();) {
        int codepoint = value.codePointAt(i);
        if (!InfraHelper.isAsciiDigit(codepoint)) {
          return false;
        }
        i += Character.charCount(codepoint);
      }
      return true;
    }
    return false;
  }

  /**
   * A forbidden domain code point is a forbidden host code point, a C0 control, U+0025 (%), or
   * U+007F DELETE.
   */
  public static boolean isForbiddenDomainCodePoint(int codepoint) {
    return isForbiddenHostCodePoint(codepoint) || InfraHelper.isC0Control(codepoint)
        || codepoint == CodepointHelper.CP_PERCENT || codepoint == 0x007F;
  }

  /**
   * A forbidden host code point is U+0000 NULL, U+0009 TAB, U+000A LF, U+000D CR, <br/>
   * U+0020 SPACE, U+0023 (#), U+002F (/), U+003A (:), <br/>
   * U+003C (<), U+003E (>), U+003F (?), U+0040 (@), <br/>
   * U+005B ([), U+005C (\), U+005D (]), U+005E (^), <br/>
   * or U+007C (|).
   */
  public static boolean isForbiddenHostCodePoint(int codepoint) {
    return codepoint == 0x0000 || codepoint == 0x0009 || codepoint == 0x000A || codepoint == 0x000D
        || codepoint == 0x0020 || codepoint == 0x0023 || codepoint == 0x002F || codepoint == 0x003A
        || codepoint == 0x003C || codepoint == 0x003E || codepoint == 0x003F || codepoint == 0x0040
        || codepoint == 0x005B || codepoint == 0x005C || codepoint == 0x005D || codepoint == 0x005E
        || codepoint == 0x007C;
  }

  /**
   * The C0 control percent-encode set are the C0 controls and all code points greater than U+007E
   * (~).
   *
   * @param codepoint
   * @return
   */
  public static boolean isInC0ControlPercentEncodeSet(int codepoint) {
    return InfraHelper.isC0Control(codepoint) || codepoint > 0x007E;
  }

  /**
   * The component percent-encode set is the userinfo percent-encode set and U+0024 ($) to U+0026
   * (&), inclusive, U+002B (+), and U+002C (,).
   *
   * @param codepoint
   * @return
   */
  public static boolean isInComponentPercentEncodeSet(int codepoint) {
    return isInUserInfoPercentEncodeSet(codepoint) || codepoint == 0x0024 || codepoint == 0x0025
        || codepoint == 0x0026 || codepoint == 0x002B || codepoint == 0x002C;
  }

  /**
   * The fragment percent-encode set is the C0 control percent-encode set and U+0020 SPACE, U+0022
   * ("), U+003C (<), U+003E (>), and U+0060 (`).
   *
   * @param codepoint
   * @return
   */
  public static boolean isInFragmentPercentEncodeSet(int codepoint) {
    return isInC0ControlPercentEncodeSet(codepoint) || codepoint == CodepointHelper.CP_SPACE
        || codepoint == CodepointHelper.CP_QUOTATION_MARK
        || codepoint == CodepointHelper.CP_LESS_THAN || codepoint == CodepointHelper.CP_GREATER_THAN
        || codepoint == 0x0060 /* ' */
    ;
  }

  /**
   * The path percent-encode set is the query percent-encode set and U+003F (?), U+0060 (`), U+007B
   * ({), and U+007D (}).
   *
   * @param codepoint
   * @return
   */
  public static boolean isInPathPercentEncodeSet(int codepoint) {
    return isQueryPercentEncodeSet(codepoint) || codepoint == CodepointHelper.CP_QUESTION_MARK
        || codepoint == 0x0060 || codepoint == 0x007B/* { */
        || codepoint == 0x007D/* } */
    ;
  }

  /**
   * The userinfo percent-encode set is the path percent-encode set and U+002F (/), U+003A (:),
   * U+003B (;), U+003D (=), U+0040 (@), U+005B ([) to U+005E (^), inclusive, and U+007C (|)
   *
   * @param codepoint
   * @return
   */
  public static boolean isInUserInfoPercentEncodeSet(int codepoint) {
    return isInPathPercentEncodeSet(codepoint) || codepoint == 0x002F /* / */
        || codepoint == 0x003A /* : */ || codepoint == 0x003B /* ; */ || codepoint == 0x003D /* = */
        || codepoint == 0x0040 /* @ */ || codepoint == 0x005B /* [ */ || codepoint == 0x005C /* \ */
        || codepoint == 0x005D /* ] */ || codepoint == 0x005E /* ^ */ || codepoint == 0x007C /* | */
    ;
  }

  /**
   * The application/x-www-form-urlencoded percent-encode set is the component percent-encode set
   * and U+0021 (!), U+0027 (') to U+0029 RIGHT PARENTHESIS, inclusive, and U+007E (~).
   */
  public static boolean isInUrlEncodedPercentEncodeSet(int codepoint) {
    return isInComponentPercentEncodeSet(codepoint) || codepoint == 0x0021
        || (codepoint >= 0x0027 && codepoint <= 0x0029) || codepoint == 0x007E;
  }

  /**
   *
   * A noncharacter is a code point that is in the range U+FDD0 to U+FDEF, inclusive, or U+FFFE,
   * U+FFFF, U+1FFFE, U+1FFFF, U+2FFFE, U+2FFFF, U+3FFFE, U+3FFFF, U+4FFFE, U+4FFFF, U+5FFFE,
   * U+5FFFF, U+6FFFE, U+6FFFF, U+7FFFE, U+7FFFF, U+8FFFE, U+8FFFF, U+9FFFE, U+9FFFF, U+AFFFE,
   * U+AFFFF, U+BFFFE, U+BFFFF, U+CFFFE, U+CFFFF, U+DFFFE, U+DFFFF, U+EFFFE, U+EFFFF, U+FFFFE,
   * U+FFFFF, U+10FFFE, or U+10FFFF.
   */
  public static boolean isNonCharacter(int codePoint) {
    return (codePoint >= 0xFDD0 && codePoint <= 0xFDEF) || codePoint == 0xFFFE
        || codePoint == 0xFFFF || codePoint == 0x1FFFE || codePoint == 0x1FFFF
        || codePoint == 0x2FFFE || codePoint == 0x2FFFF || codePoint == 0x3FFFE
        || codePoint == 0x3FFFF || codePoint == 0x4FFFE || codePoint == 0x4FFFF
        || codePoint == 0x5FFFE || codePoint == 0x5FFFF || codePoint == 0x6FFFE
        || codePoint == 0x6FFFF || codePoint == 0x7FFFE || codePoint == 0x7FFFF
        || codePoint == 0x8FFFE || codePoint == 0x8FFFF || codePoint == 0x9FFFE
        || codePoint == 0x9FFFF || codePoint == 0xAFFFE || codePoint == 0xAFFFF
        || codePoint == 0xBFFFE || codePoint == 0xBFFFF || codePoint == 0xCFFFE
        || codePoint == 0xCFFFF || codePoint == 0xDFFFE || codePoint == 0xDFFFF
        || codePoint == 0xEFFFE || codePoint == 0xEFFFF || codePoint == 0xFFFFE
        || codePoint == 0xFFFFF || codePoint == 0x10FFFE || codePoint == 0x10FFFF;
  }

  /**
   * The query percent-encode set is the C0 control percent-encode set and U+0020 SPACE, U+0022 ("),
   * U+0023 (#), U+003C (<), and U+003E (>).
   *
   * @param codepoint
   * @return
   */
  public static boolean isQueryPercentEncodeSet(int codepoint) {
    return isInC0ControlPercentEncodeSet(codepoint) || codepoint == CodepointHelper.CP_SPACE
        || codepoint == CodepointHelper.CP_QUOTATION_MARK || codepoint == CodepointHelper.CP_HASH
        || codepoint == CodepointHelper.CP_LESS_THAN
        || codepoint == CodepointHelper.CP_GREATER_THAN;
  }

  // The special-query percent-encode set is the query percent-encode set and U+0027 (').
  public static boolean isSpecialQueryPercentEncodeSet(int codepoint) {
    return isQueryPercentEncodeSet(codepoint) || codepoint == CodepointHelper.CP_APOSTROPHE;
  }

  /**
   * The URL code points are ASCII alphanumeric, U+0021 (!), U+0024 ($), U+0026 (&), U+0027 ('),
   * U+0028 LEFT PARENTHESIS, U+0029 RIGHT PARENTHESIS, U+002A (*), U+002B (+), U+002C (,), U+002D
   * (-), U+002E (.), U+002F (/), U+003A (:), U+003B (;), U+003D (=), U+003F (?), U+0040 (@), U+005F
   * (_), U+007E (~), and code points in the range U+00A0 to U+10FFFD, inclusive, excluding
   * surrogates and noncharacters.
   *
   * @return
   */
  public static boolean isUrlCodepoint(int codePoint) {
    return InfraHelper.isAsciiAlphanumeric(codePoint) || codePoint == 0x0021 || codePoint == 0x0024
        || codePoint == 0x0026 || codePoint == 0x0027 || codePoint == 0x0028 || codePoint == 0x0029
        || codePoint == 0x002A || codePoint == 0x002B || codePoint == 0x002C || codePoint == 0x002D
        || codePoint == 0x002E || codePoint == 0x002F || codePoint == 0x003A || codePoint == 0x003B
        || codePoint == 0x003D || codePoint == 0x003F || codePoint == 0x0040 || codePoint == 0x005F
        || codePoint == 0x007E || (codePoint >= 0x00A0 && codePoint <= 0x10FFFD)
        || !InfraHelper.isSurrogate(codePoint) || !isNonCharacter(codePoint);
  }
}
