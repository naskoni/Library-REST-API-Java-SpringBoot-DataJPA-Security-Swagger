package com.naskoni.library.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationErrorDTO {

  private final List<FieldErrorDTO> fieldErrors = new ArrayList<>();

  public void addFieldError(String path, String message) {
    FieldErrorDTO error = new FieldErrorDTO(path, message);
    fieldErrors.add(error);
  }

  public List<FieldErrorDTO> getFieldErrors() {
    return Collections.unmodifiableList(fieldErrors);
  }
}
