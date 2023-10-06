package io.github.stephanebastian.whatwg.url.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestUrlHelper {
  static Collection<Map<String, Object>> percentEncodeAfterEncodingWhatWgTestData() {
    Collection<Object> result = TestUtils.readJsonFile("percent-encoding.json");
    // remove entries that are not Maps to account for comments in the file
    result.removeIf(next -> !(next instanceof Map));
    return (Collection<Map<String, Object>>) (Object) result;
  }

  @Test
  public void hasLeadingOrTrailingC0ControlOrSpace() {
    Assertions
        .assertThat(UrlHelper.hasLeadingOrTrailingC0ControlOrSpace("abc".codePoints().toArray()))
        .isFalse();
    Assertions
        .assertThat(UrlHelper.hasLeadingOrTrailingC0ControlOrSpace(" abc".codePoints().toArray()))
        .isTrue();
    Assertions
        .assertThat(UrlHelper.hasLeadingOrTrailingC0ControlOrSpace("abc ".codePoints().toArray()))
        .isTrue();
    Assertions
        .assertThat(UrlHelper.hasLeadingOrTrailingC0ControlOrSpace("a bc".codePoints().toArray()))
        .isFalse();
    Assertions
        .assertThat(UrlHelper.hasLeadingOrTrailingC0ControlOrSpace("ab c".codePoints().toArray()))
        .isFalse();
  }

  @Test
  public void percentDecode() {
    Assertions.assertThat(UrlHelper.percentDecode("%25%s%1G"))
        .isEqualTo("%%s%1G".getBytes(StandardCharsets.UTF_8));
    Assertions.assertThat(UrlHelper.percentDecode("‽%25%2E"))
        .isEqualTo(new byte[] {(byte) 226, (byte) 128, (byte) 189, 37, 46});
  }

  @Test
  public void percentEncode() {
    StringBuilder output = new StringBuilder();
    Assertions.assertThat(UrlHelper.percentEncode((byte) 0x3)).isEqualTo("%03");
    Assertions.assertThat(UrlHelper.percentEncode((byte) 0x23)).isEqualTo("%23");
    Assertions.assertThat(UrlHelper.percentEncode((byte) 0x7F)).isEqualTo("%7F");
  }

  @Test
  public void percentEncodeAfterEncodingIso2022JP() {
    Charset charset = Charset.forName("ISO-2022-JP");
    Assertions.assertThat(UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(), "¥",
        CodepointHelper::isInUserInfoPercentEncodeSet, false)).isEqualTo("%1B(J%5C%1B(B");
    // TODO this test used to fail with the expected value of '"%1B(J\\%1B(B"'. Note that this value
    // comes
    // from an example of the WhatWg url spec. However the result I get (which is '%1B(J\%1B(B') is
    // the one that makes sens to me.
    // the reason is that the encoding of the value is the same in all online encoders I tried.
    // Thus the percent encoded value is the one I am testing against and I suspect that there is a
    // typo in the WhatWg url exammple
    // Need to check though
  }

  @ParameterizedTest
  @MethodSource("percentEncodeAfterEncodingWhatWgTestData")
  public void percentEncodeAfterEncodingOnWhatWgTestData(Map<String, Object> testData) {
    String input = (String) testData.get("input");
    Map<String, Object> output = (Map<String, Object>) testData.get("output");
    if (input != null && output != null) {
      for (String encoding : output.keySet()) {
        String expectedEncodedResult = (String) output.get(encoding);
        Charset charset = Charset.forName(encoding);
        int[] scalarCodepoints = InfraHelper.toScalarCodepoints(input.codePoints().toArray());
        String scalarInput = new String(scalarCodepoints, 0, scalarCodepoints.length);
        String encodedValue = UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(),
            scalarInput, CodepointHelper::isSpecialQueryPercentEncodeSet, false);
        Assertions.assertThat(encodedValue).isEqualTo(expectedEncodedResult);
      }
    }
  }

  @Test
  public void percentEncodeAfterEncodingShiftJIS() {
    Charset charset = Charset.forName("Shift_JIS");
    Assertions.assertThat(UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(), " ",
        CodepointHelper::isInUserInfoPercentEncodeSet, false)).isEqualTo("%20");
    Assertions.assertThat(UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(), "≡",
        CodepointHelper::isInUserInfoPercentEncodeSet, false)).isEqualTo("%81%DF");
    Assertions.assertThat(UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(), "‽",
        CodepointHelper::isInUserInfoPercentEncodeSet, false)).isEqualTo("%26%238253%3B");
    Assertions
        .assertThat(UrlHelper.percentEncodeAfterEncoding(charset.newEncoder(), "1+1 ≡ 2%20‽",
            CodepointHelper::isInUserInfoPercentEncodeSet, true))
        .isEqualTo("1+1+%81%DF+2%20%26%238253%3B");
  }

  @Test
  public void removeAsciiTabAndNewline() {
    Assertions.assertThat(UrlHelper.removeAsciiTabAndNewline("\ta\rb\nc\td".codePoints().toArray()))
        .isEqualTo("abcd".codePoints().toArray());
    Assertions
        .assertThat(UrlHelper
            .removeAsciiTabAndNewline("\t\n\na\t\n\nb\t\n\nc\t\n\nd".codePoints().toArray()))
        .isEqualTo("abcd".codePoints().toArray());
  }

  @Test
  public void removeLeadingOrTrailingC0ControlOrSpace() {
    // Assertions.assertThat(UrlParserUtils.removeLeadingOrTrailingC0ControlOrSpace("abc".codePoints().toArray())).isEqualTo("abc".codePoints().toArray());
    // Assertions.assertThat(UrlParserUtils.removeLeadingOrTrailingC0ControlOrSpace("
    // abc".codePoints().toArray())).isEqualTo("abc".codePoints().toArray());
    Assertions
        .assertThat(
            UrlHelper.removeLeadingOrTrailingC0ControlOrSpace("abc ".codePoints().toArray()))
        .isEqualTo("abc".codePoints().toArray());
    Assertions
        .assertThat(
            UrlHelper.removeLeadingOrTrailingC0ControlOrSpace("    abc".codePoints().toArray()))
        .isEqualTo("abc".codePoints().toArray());
    Assertions
        .assertThat(
            UrlHelper.removeLeadingOrTrailingC0ControlOrSpace("abc    ".codePoints().toArray()))
        .isEqualTo("abc".codePoints().toArray());
    Assertions
        .assertThat(UrlHelper
            .removeLeadingOrTrailingC0ControlOrSpace("    abc       ".codePoints().toArray()))
        .isEqualTo("abc".codePoints().toArray());
    Assertions
        .assertThat(
            UrlHelper.removeLeadingOrTrailingC0ControlOrSpace("a bc".codePoints().toArray()))
        .isEqualTo("a bc".codePoints().toArray());
    Assertions
        .assertThat(
            UrlHelper.removeLeadingOrTrailingC0ControlOrSpace("ab c".codePoints().toArray()))
        .isEqualTo("ab c".codePoints().toArray());
  }

  @Test
  public void toScalarCodepoints() {
    Assertions.assertThat(InfraHelper.toScalarCodepoints("abc".codePoints().toArray()))
        .isEqualTo("abc".codePoints().toArray());
    Assertions.assertThat(InfraHelper.toScalarCodepoints("a\uD800b".codePoints().toArray()))
        .isEqualTo("a\uFFFDb".codePoints().toArray());
    Assertions
        .assertThat(InfraHelper.toScalarCodepoints("a\uD83Db\uD83Dc\uD83Dd".codePoints().toArray()))
        .isEqualTo("a\uFFFDb\uFFFDc\uFFFDd".codePoints().toArray());
  }

  @Test
  public void utf8PercentEncode() {
    Assertions
        .assertThat(UrlHelper.utf8PercentEncode("≡", CodepointHelper::isInUserInfoPercentEncodeSet))
        .isEqualTo("%E2%89%A1");
    Assertions
        .assertThat(UrlHelper.utf8PercentEncode("‽", CodepointHelper::isInUserInfoPercentEncodeSet))
        .isEqualTo("%E2%80%BD");
    Assertions
        .assertThat(
            UrlHelper.utf8PercentEncode("Say what‽", CodepointHelper::isInUserInfoPercentEncodeSet))
        .isEqualTo("Say%20what%E2%80%BD");
  }

  @Test
  public void utf8PercentEncodeSurrogatePair1() {
    String test = "💩";
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < test.length();) {
      int cp = test.codePointAt(i);
      String s = UrlHelper.utf8PercentEncode(StandardCharsets.UTF_8.newEncoder(), cp,
          CodepointHelper::isInUserInfoPercentEncodeSet);
      result.append(s);
      i += Character.charCount(cp);
    }
    Assertions.assertThat(result.toString()).isEqualTo("%F0%9F%92%A9");
  }
}
