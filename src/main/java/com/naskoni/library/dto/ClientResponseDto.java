package com.naskoni.library.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientResponseDto extends AbstractResponseDto {

  private String name;

  private String pid;

  private LocalDate birthdate;

  private String createdBy;
}
