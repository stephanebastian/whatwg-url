/*
 * Copyright 2023 - Stephane Bastian - stephane.bastian.dev@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
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
