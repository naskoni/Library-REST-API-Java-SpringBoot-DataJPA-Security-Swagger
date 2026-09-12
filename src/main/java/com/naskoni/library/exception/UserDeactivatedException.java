package com.naskoni.library.exception;

import java.io.Serial;

public class UserDeactivatedException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -693971327265038962L;

  public UserDeactivatedException(String message) {
    super(message);
  }
}
