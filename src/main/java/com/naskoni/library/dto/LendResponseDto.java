package com.naskoni.library.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LendResponseDto extends AbstractResponseDto {

  private BookResponseDto book;

  private ClientResponseDto client;

  private LocalDate lendingDate;

  private LocalDate returnDate;
}
