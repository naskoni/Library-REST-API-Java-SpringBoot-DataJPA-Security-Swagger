package com.naskoni.library.exception;

import java.io.Serial;

public class DuplicateException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -786012424912401959L;

  public DuplicateException(String message) {
    super(message);
  }
}
