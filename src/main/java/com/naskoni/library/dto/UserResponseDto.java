package com.naskoni.library.dto;

import com.naskoni.library.enumeration.Role;
import com.naskoni.library.enumeration.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponseDto extends AbstractResponseDto {

  private String name;

  private String username;

  private Status status;

  private Role role;
}
