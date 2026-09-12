package com.naskoni.library.dto;

import com.naskoni.library.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
