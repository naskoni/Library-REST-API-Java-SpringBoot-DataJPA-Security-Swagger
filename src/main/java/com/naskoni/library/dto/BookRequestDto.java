package com.naskoni.library.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BookRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String name;

  @NotBlank
  @Size(min = 1, max = 50)
  private String author;

  @Min(1800)
  @Max(2100)
  private int year;

  @Size(min = 10, max = 13)
  private String isbn;
}
