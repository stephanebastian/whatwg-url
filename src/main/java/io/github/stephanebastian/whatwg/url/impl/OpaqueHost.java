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

class OpaqueHost implements Host {
  private String host;

  private OpaqueHost(String host) {
    this.host = Objects.requireNonNull(host);
  }

  public final static OpaqueHost create(String host) {
    return new OpaqueHost(host);
  }

  public String host() {
    return host;
  }

  public String toString() {
    return host;
  }
}
