package com.naskoni.library.service.impl;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.FileTypesDto;
import com.naskoni.library.entity.Book;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.exporter.Exporter;
import com.naskoni.library.exporter.ExporterFactory;
import com.naskoni.library.repository.BookRepository;
import com.naskoni.library.repository.LendRepository;
import com.naskoni.library.service.BookService;
import com.naskoni.library.specification.SpecificationsBuilder;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  public static final String BOOK_NOT_FOUND = "Book with id: %d could not be found";
  public static final String BOOK_IN_USE = "Book with id: %d is currently in use";

  private final BookRepository bookRepository;
  private final LendRepository lendRepository;
  private final ExporterFactory exporterFactory;

  @Override
  @Transactional
  public BookResponseDto create(BookRequestDto bookDto) {
    var book = mapToEntity(bookDto);
    var savedBook = bookRepository.save(book);
    return mapToDto(savedBook);
  }

  @Override
  @Transactional
  public BookResponseDto update(Long id, BookRequestDto bookDto) {
    var book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND.formatted(id)));

    BeanUtils.copyProperties(bookDto, book);

    var savedBook = bookRepository.save(book);
    return mapToDto(savedBook);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    var book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND.formatted(id)));

    var lend = lendRepository.findByBook(book);
    if (lend.isPresent()) {
      throw new CurrentlyInUseException(BOOK_IN_USE.formatted(id));
    }

    bookRepository.delete(book);
  }

  @Override
  @Transactional(readOnly = true)
  public BookResponseDto findOne(Long id) {
    var book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND.formatted(id)));

    return mapToDto(book);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BookResponseDto> findAll(String search, Pageable pageable) {
    SpecificationsBuilder<Book> builder = new SpecificationsBuilder<>();
    Matcher matcher = Helper.getMatcher(search);

    while (matcher.find()) {
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    Specification<Book> spec = builder.build();
    Page<Book> books = bookRepository.findAll(spec, pageable);

    return books.map(this::mapToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] export(String type) throws IOException {
    List<Book> books = bookRepository.findAll();
    Exporter exporter = exporterFactory.newInstance(type);

    return exporter.export(books);
  }

  @Override
  public FileTypesDto retrieveSupportedFileTypes() {
    var dto = new FileTypesDto();
    dto.setSupportedFileTypes(exporterFactory.getTypes());

    return dto;
  }

  private BookResponseDto mapToDto(Book book) {
    var bookDto = new BookResponseDto();
    BeanUtils.copyProperties(book, bookDto);

    return bookDto;
  }

  Book mapToEntity(BookRequestDto bookDto) {
    var book = new Book();
    BeanUtils.copyProperties(bookDto, book);
    return book;
  }
}