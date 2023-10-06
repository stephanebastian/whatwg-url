package io.github.stephanebastian.whatwg.url.impl;

class EmptyHost implements Host {
  private final static EmptyHost instance = new EmptyHost();

  private EmptyHost() {
  }

  public final static EmptyHost create() {
    return instance;
  }

  public String toString() {
    return "";
  }
}
