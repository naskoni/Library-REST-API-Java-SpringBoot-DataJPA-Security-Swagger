package com.naskoni.library.service;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.FileTypesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface BookService {

  BookResponseDto create(BookRequestDto bookDto);

  BookResponseDto update(Long id, BookRequestDto bookDto);

  void delete(Long id);

  BookResponseDto findOne(Long id);

  Page<BookResponseDto> findAll(String search, Pageable pageable);

  byte[] export(String type) throws IOException;

  FileTypesDto retrieveSupportedFileTypes();
}
