package io.github.stephanebastian.whatwg.url.impl;

import java.util.Objects;

class Ipv4Address implements Host {
  private int ip;

  private Ipv4Address(int ip) {
    this.ip = Objects.requireNonNull(ip);
  }

  public static Ipv4Address create(int ip) {
    return new Ipv4Address(ip);
  }

  public int ip() {
    return ip;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    SerializerHelper.serializeHost(this, result);
    return result.toString();
  }
}
