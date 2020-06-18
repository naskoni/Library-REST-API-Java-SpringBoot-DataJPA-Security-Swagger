package com.naskoni.library.exception;

public class DuplicateException extends RuntimeException {

  private static final long serialVersionUID = -786012424912401959L;

  public DuplicateException(String message) {
    super(message);
  }
}
