package io.github.stephanebastian.whatwg.url.impl;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInfraHelper {
  @Test
  void strictSplit() {
    Assertions.assertThat(InfraHelper.strictSplit("a.B.c", '.'))
        .isEqualTo(Arrays.asList("a", "B", "c"));
    Assertions.assertThat(InfraHelper.strictSplit(".a.B..c.", '.'))
        .isEqualTo(Arrays.asList("", "a", "B", "", "c", ""));
  }

  @Test
  void toHexChars() {
    Assertions.assertThat(InfraHelper.toHexChars((byte) 0)).isEqualTo(new char[] {'0', '0'});
    Assertions.assertThat(InfraHelper.toHexChars((byte) 15)).isEqualTo(new char[] {'0', 'F'});
    Assertions.assertThat(InfraHelper.toHexChars((byte) 16)).isEqualTo(new char[] {'1', '0'});
    Assertions.assertThat(InfraHelper.toHexChars((byte) 255)).isEqualTo(new char[] {'F', 'F'});
  }
}
