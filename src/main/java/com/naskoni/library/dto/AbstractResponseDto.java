package com.naskoni.library.dto;

import java.time.Instant;
import lombok.Data;

@Data
public abstract class AbstractResponseDto {

  private Long id;

  private Instant created;

  private Instant updated;
}