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

class Ipv6Address implements Host {
  private short[] ip;

  private Ipv6Address(short[] ip) {
    this.ip = Objects.requireNonNull(ip);
  }

  public static Ipv6Address create(short[] ip) {
    return new Ipv6Address(ip);
  }

  public short[] ip() {
    return ip;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    SerializerHelper.serializeHost(this, result);
    return result.toString();
  }
}
