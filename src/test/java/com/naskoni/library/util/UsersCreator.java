package com.naskoni.library.util;

import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.entity.User;
import com.naskoni.library.enumeration.Role;
import com.naskoni.library.enumeration.Status;

import java.util.ArrayList;
import java.util.List;

public class UsersCreator {

  public static User getUser() {
    var user = new User();
    user.setId(1L);
    user.setName("name");
    user.setUsername("user");
    user.setPassword("pass");
    user.setRole(Role.ROLE_USER);

    return user;
  }

  public static List<User> getUsers() {
    List<User> users = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      users.add(getUser());
    }

    return users;
  }

  public static UserRequestDto getUserRequestDto() {
    var user = new UserRequestDto();
    user.setName("name");
    user.setUsername("user");
    user.setPassword("pass");
    user.setRole(Role.ROLE_USER);
    return user;
  }

  public static UserResponseDto getUserResponseDto() {
    var user = new UserResponseDto();
    user.setId(1L);
    user.setName("name");
    user.setUsername("user");
    user.setRole(Role.ROLE_USER);
    user.setStatus(Status.ACTIVE);
    return user;
  }

  public static List<UserResponseDto> getUserResponseDtos() {
    List<UserResponseDto> users = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      users.add(getUserResponseDto());
    }

    return users;
  }
}
