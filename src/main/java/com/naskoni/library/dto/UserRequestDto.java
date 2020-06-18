package com.naskoni.library.dto;

import com.naskoni.library.enumeration.Role;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String name;

  @NotBlank
  @Size(min = 1, max = 50)
  private String username;

  @NotBlank
  @Size(min = 4, max = 50)
  private String password;

  private Role role;
}
