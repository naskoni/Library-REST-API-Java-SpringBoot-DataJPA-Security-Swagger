package com.naskoni.library.service;

import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

  ClientResponseDto create(ClientRequestDto clientDto);

  ClientResponseDto update(Long id, ClientRequestDto clientDto);

  void delete(Long id);

  ClientResponseDto findOne(Long id);

  Page<ClientResponseDto> findAll(String search, Pageable pageable);
}
