package com.naskoni.library.exception;

import java.io.Serial;

public class NotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -8114875267754096095L;

  public NotFoundException(String message) {
    super(message);
  }
}
