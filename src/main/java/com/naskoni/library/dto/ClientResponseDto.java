package com.naskoni.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientResponseDto extends AbstractResponseDto {

  private String name;

  private String pid;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date birthdate;

  private String createdBy;
}
