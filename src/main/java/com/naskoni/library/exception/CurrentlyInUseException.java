package com.naskoni.library.exception;

public class CurrentlyInUseException extends RuntimeException {

  private static final long serialVersionUID = 1025537995195594035L;

  public CurrentlyInUseException(String message) {
    super(message);
  }
}
