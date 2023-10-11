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

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;

/**
 * This class is a helper to make it easier to deal with codepoints
 */
public class Codepoints {
  int[] codepoints;
  int pointer;

  public Codepoints(String value) {
    Objects.requireNonNull(value);
    this.codepoints = value.codePoints().toArray();
    this.pointer = 0;
  }

  public Codepoints(int[] codepoints) {
    this.codepoints = Objects.requireNonNull(codepoints);
    this.pointer = 0;
  }

  int codepoint() {
    return codepointAt(pointer());
  }

  int codepointAt(int position) {
    if (position < 0) {
      return CodepointHelper.CP_BOF;
    }
    if (position >= codepoints.length) {
      return CodepointHelper.CP_EOF;
    }
    return codepoints[position];
  }

  boolean codepointIs(int codePoint) {
    return codepoint() == codePoint;
  }

  boolean codepointIs(IntPredicate predicate) {
    return predicate.test(codepoint());
  }

  boolean codepointIsNot(IntPredicate predicate) {
    return !codepointIs(predicate);
  }

  boolean codepointIsNot(int codePoint) {
    return !codepointIs(codePoint);
  }

  boolean codepointIsOneOf(int... codePoints) {
    for (int codePoint : codePoints) {
      if (codepointIs(codePoint)) {
        return true;
      }
    }
    return false;
  }

  void decreasePointerBy(int value) {
    pointer(pointer() - value);
  }

  void decreasePointerByOne() {
    pointer(pointer() - 1);
  }

  void increasePointerByOne() {
    pointer(pointer() + 1);
  }

  boolean isEof() {
    return codepoint() == CodepointHelper.CP_EOF;
  }

  int length() {
    return codepoints.length;
  }

  int pointer() {
    return pointer;
  }

  void pointer(int position) {
    this.pointer = position;
  }

  int remaining() {
    return remaining(pointer());
  }

  private int remaining(int position) {
    int result = length() - position;
    return result >= 0 ? result : 0;
  }

  boolean remainingMatch(int numberOfCodepointsToMatch, BiPredicate<Integer, Integer> predicate) {
    if (numberOfCodepointsToMatch > 0 && remaining(pointer() + 1) >= numberOfCodepointsToMatch) {
      for (int i = 0; i < numberOfCodepointsToMatch; i++) {
        int codepoint = codepointAt(pointer() + i + 1);
        boolean result = predicate.test(i, codepoint);
        if (!result) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  void startOver() {
    pointer(-1);
  }
}
