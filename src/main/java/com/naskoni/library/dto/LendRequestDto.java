package com.naskoni.library.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LendRequestDto {

  @NotNull
  private Long bookId;

  @NotNull
  private Long clientId;

  private LocalDate lendingDate;

  private LocalDate returnDate;
}
