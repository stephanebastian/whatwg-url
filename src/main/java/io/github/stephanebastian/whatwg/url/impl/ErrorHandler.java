package io.github.stephanebastian.whatwg.url.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ErrorHandler {
  private Collection<UrlException> errors;

  public ErrorHandler() {
  }

  public void error(UrlException error) {
    Objects.requireNonNull(error);
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(error);
  }

  public Collection<UrlException> errors() {
    return errors != null ? errors : Collections.emptyList();
  }
}
