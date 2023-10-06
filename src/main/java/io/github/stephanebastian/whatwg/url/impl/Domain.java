package io.github.stephanebastian.whatwg.url.impl;

import java.util.Objects;

class Domain implements Host {
  private String host;

  private Domain(String host) {
    this.host = Objects.requireNonNull(host);
  }

  public final static Domain create(String host) {
    return new Domain(host);
  }

  public String host() {
    return host;
  }

  public String toString() {
    return host;
  }
}
