package com.naskoni.library.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;

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
