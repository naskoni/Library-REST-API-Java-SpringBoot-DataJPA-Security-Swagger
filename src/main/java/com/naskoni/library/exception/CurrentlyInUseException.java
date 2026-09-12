package com.naskoni.library.exception;

import java.io.Serial;

public class CurrentlyInUseException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1025537995195594035L;

  public CurrentlyInUseException(String message) {
    super(message);
  }
}
