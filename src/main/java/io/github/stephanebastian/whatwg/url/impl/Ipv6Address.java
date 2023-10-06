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
