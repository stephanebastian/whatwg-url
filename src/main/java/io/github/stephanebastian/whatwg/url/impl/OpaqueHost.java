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
