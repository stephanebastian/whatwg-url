package io.github.stephanebastian.whatwg.url.impl;

import java.util.Objects;

class UrlSearchParam {
  private final String name;
  private String value;

  public UrlSearchParam(String name, String value) {
    this.name = Objects.requireNonNull(name);
    this.value = value;
  }

  public String name() {
    return name;
  }

  public String value() {
    return value;
  }

  public void value(String value) {
    this.value = value;
  }
}
