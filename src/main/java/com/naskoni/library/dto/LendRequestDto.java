package com.naskoni.library.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class LendRequestDto {

  @NotNull
  private Long bookId;

  @NotNull
  private Long clientId;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.DATE)
  private Date lendingDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.DATE)
  private Date returnDate;
}
