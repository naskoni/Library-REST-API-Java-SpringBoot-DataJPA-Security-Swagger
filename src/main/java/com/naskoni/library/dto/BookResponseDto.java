package com.naskoni.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookResponseDto extends AbstractResponseDto {

  private String name;

  private String author;

  private int year;

  private String isbn;
}
