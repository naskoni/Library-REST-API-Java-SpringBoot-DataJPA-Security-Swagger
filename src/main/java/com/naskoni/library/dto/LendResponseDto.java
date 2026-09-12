package com.naskoni.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LendResponseDto extends AbstractResponseDto {

  private BookResponseDto book;

  private ClientResponseDto client;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date lendingDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date returnDate;
}
