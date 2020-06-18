package com.naskoni.library.service;

import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LendService {

  LendResponseDto create(LendRequestDto lendRequestDto);

  LendResponseDto update(Long id, LendRequestDto lendRequestDto);

  LendResponseDto findOne(Long id);

  Page<LendResponseDto> findAll(String search, Pageable pageable);
}
