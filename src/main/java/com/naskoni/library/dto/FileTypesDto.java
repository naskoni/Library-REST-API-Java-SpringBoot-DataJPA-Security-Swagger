package com.naskoni.library.dto;

import java.util.Set;
import lombok.Data;

@Data
public class FileTypesDto {

  private Set<String> supportedFileTypes;
}
