package com.naskoni.library.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ClientRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String name;

  @Size(min = 10, max = 10)
  private String pid;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.DATE)
  private Date birthdate;
}
