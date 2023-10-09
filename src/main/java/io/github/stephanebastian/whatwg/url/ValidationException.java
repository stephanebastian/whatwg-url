package io.github.stephanebastian.whatwg.url;

import java.util.Objects;

public class ValidationException extends RuntimeException {
  private final ValidationError validationError;

  public ValidationException(ValidationError validationError) {
    super();
    this.validationError = Objects.requireNonNull(validationError);
  }

  public ValidationError validationError() {
    return validationError;
  }
}
