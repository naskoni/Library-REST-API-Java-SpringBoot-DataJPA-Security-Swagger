package com.naskoni.library.dto;

import lombok.Data;

import java.util.Set;

@Data
public class FileTypesDto {

  private Set<String> supportedFileTypes;
}
