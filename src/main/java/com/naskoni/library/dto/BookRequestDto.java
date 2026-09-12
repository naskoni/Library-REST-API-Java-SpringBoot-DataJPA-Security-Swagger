package com.naskoni.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
