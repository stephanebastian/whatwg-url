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

import java.nio.ByteBuffer;
import java.nio.charset.*;

public class EncodingHelper {
  public static CharsetDecoder getDecoder(Charset charset) {
    return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT);
  }

  public static CharsetEncoder getEncoder(Charset charset) {
    return charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT);
  }

  /**
   * To UTF-8 decode without BOM an I/O queue of bytes ioQueue given an optional
   * I/O queue of scalar values output (default « »), run these steps:
   * <ul>
   *   <li>1) Process a queue with an instance of UTF-8’s decoder, ioQueue, output, and "replacement".</li>
   *   <li>2) Return output.</li>
   * </ul>
   *
   * @param input the input to decode
   * @return the decoded input as a string
   */
  public static String utf8DecodeWithoutBom(byte[] input) {
    StringBuilder result = new StringBuilder();
    InfraHelper.doDecode(getDecoder(StandardCharsets.UTF_8), ByteBuffer.wrap(input),
        InfraHelper.ErrorMode.REPLACEMENT, result::append);
    return result.toString();
  }
}
