package com.naskoni.library.service;

import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserResponseDto create(UserRequestDto user);

  UserResponseDto update(Long id, UserRequestDto user);

  UserResponseDto deactivate(Long id);

  UserResponseDto findOne(Long id);

  Page<UserResponseDto> findAll(String search, Pageable pageable);
}
