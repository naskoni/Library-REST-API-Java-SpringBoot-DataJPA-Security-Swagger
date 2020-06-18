package com.naskoni.library.exception;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8114875267754096095L;

  public NotFoundException(String message) {
    super(message);
  }
}
