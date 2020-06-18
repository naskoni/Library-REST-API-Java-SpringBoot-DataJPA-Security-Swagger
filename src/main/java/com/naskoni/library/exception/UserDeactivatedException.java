package com.naskoni.library.exception;

public class UserDeactivatedException extends RuntimeException {

  private static final long serialVersionUID = -693971327265038962L;

  public UserDeactivatedException(String message) {
    super(message);
  }
}
