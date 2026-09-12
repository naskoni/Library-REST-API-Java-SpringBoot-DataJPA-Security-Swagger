package com.naskoni.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ClientRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String name;

  @Size(min = 10, max = 10)
  private String pid;

  private LocalDate birthdate;
}
