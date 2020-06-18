package com.naskoni.library.dto;

import lombok.Data;

@Data
public class ApiErrorDto {

  private int status;
  private String message;
  private String rootCauseMessage;
}
