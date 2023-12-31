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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCodePoints {
  @Test
  public void codepointAt() {
    Codepoints input = new Codepoints("A B C D");
    Assertions.assertThat(input.length()).isEqualTo(7);
    Assertions.assertThat(input.remaining()).isEqualTo(7);
    Assertions.assertThat(input.codepointAt(-1)).isEqualTo(CodepointHelper.CP_BOF);
    Assertions.assertThat(input.codepointAt(0)).isEqualTo(65);
    Assertions.assertThat(input.codepointAt(1)).isEqualTo(32);
    Assertions.assertThat(input.codepointAt(2)).isEqualTo(66);
    Assertions.assertThat(input.codepointAt(3)).isEqualTo(32);
    Assertions.assertThat(input.codepointAt(4)).isEqualTo(67);
    Assertions.assertThat(input.codepointAt(5)).isEqualTo(32);
    Assertions.assertThat(input.codepointAt(6)).isEqualTo(68);
    Assertions.assertThat(input.codepointAt(7)).isEqualTo(CodepointHelper.CP_EOF);
    Assertions.assertThat(input.codepointAt(700)).isEqualTo(CodepointHelper.CP_EOF);
    Assertions.assertThat(input.remaining()).isEqualTo(7);
  }

  @Test
  public void emptyInput() {
    Codepoints input = new Codepoints("");
    Assertions.assertThat(input.length()).isEqualTo(0);
    Assertions.assertThat(input.remaining()).isEqualTo(0);
    Assertions.assertThat(input.codepoint()).isEqualTo(-1);
    input.increasePointerByOne();
    Assertions.assertThat(input.codepoint()).isEqualTo(-1);
    Assertions.assertThat(input.length()).isEqualTo(0);
    Assertions.assertThat(input.remaining()).isEqualTo(0);
  }

  @Test
  public void remainingMatch() {
    Codepoints input = new Codepoints("A B C D");
    Assertions.assertThat(input.length()).isEqualTo(7);
    Assertions.assertThat(input.remaining()).isEqualTo(7);
    Assertions.assertThat(input.codepoint()).isEqualTo(65);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 32 : cp == 66))
        .isTrue();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(6);
    Assertions.assertThat(input.codepoint()).isEqualTo(32);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 66 : cp == 32))
        .isTrue();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(5);
    Assertions.assertThat(input.codepoint()).isEqualTo(66);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 32 : cp == 67))
        .isTrue();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(4);
    Assertions.assertThat(input.codepoint()).isEqualTo(32);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 67 : cp == 32))
        .isTrue();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(3);
    Assertions.assertThat(input.codepoint()).isEqualTo(67);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 32 : cp == 68))
        .isTrue();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(2);
    Assertions.assertThat(input.codepoint()).isEqualTo(32);
    Assertions.assertThat(input.remainingMatch(1, (idx, cp) -> cp == 68)).isTrue();
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> idx == 0 ? cp == 68 : cp == 32))
        .isFalse();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(1);
    Assertions.assertThat(input.codepoint()).isEqualTo(68);
    Assertions.assertThat(input.remainingMatch(2, (idx, cp) -> true)).isFalse();
    input.increasePointerByOne();
    Assertions.assertThat(input.remaining()).isEqualTo(0);
    Assertions.assertThat(input.codepoint()).isEqualTo(CodepointHelper.CP_EOF);
  }
}
