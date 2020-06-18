package com.naskoni.library.service.impl;

import com.naskoni.library.dao.BookDao;
import com.naskoni.library.dao.LendDao;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.entity.Book;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.exporter.CsvFileExporter;
import com.naskoni.library.exporter.ExporterFactory;
import com.naskoni.library.specification.SpecificationsBuilder;
import com.naskoni.library.util.BooksCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BookServiceTest {

  @Mock private BookDao bookDao;
  @Mock private LendDao lendDao;
  @Mock private ExporterFactory exporterFactory;

  @InjectMocks private BookServiceImpl bookService;

  @Test
  void createShouldSuccess() {
    var bookRequestDto = BooksCreator.getBookRequestDto();
    Book book = bookService.mapToEntity(bookRequestDto);

    when(bookDao.save(book)).thenReturn(book);
    BookResponseDto bookResponseDto = bookService.create(bookRequestDto);

    assertEquals(book.getId(), bookResponseDto.getId());
    assertEquals(book.getName(), bookResponseDto.getName());
    assertEquals(book.getAuthor(), bookResponseDto.getAuthor());
    assertEquals(book.getIsbn(), bookResponseDto.getIsbn());
    assertEquals(book.getYear(), bookResponseDto.getYear());
  }

  @Test
  void updateExistentBookShouldSuccess() {
    var bookRequestDto = BooksCreator.getBookRequestDto();
    Book book = bookService.mapToEntity(bookRequestDto);

    when(bookDao.findById(anyLong())).thenReturn(Optional.of(book));
    when(bookDao.save(book)).thenReturn(book);
    BookResponseDto bookResponseDto = bookService.update(1L, bookRequestDto);

    assertEquals(book.getId(), bookResponseDto.getId());
    assertEquals(book.getName(), bookResponseDto.getName());
    assertEquals(book.getAuthor(), bookResponseDto.getAuthor());
    assertEquals(book.getIsbn(), bookResponseDto.getIsbn());
    assertEquals(book.getYear(), bookResponseDto.getYear());
  }

  @Test
  void updateNonExistentBookShouldThrowNotFoundException() {
    var bookRequestDto = BooksCreator.getBookRequestDto();
    assertThrows(NotFoundException.class, () -> bookService.update(1L, bookRequestDto));
  }

  @Test
  void deleteExistentBookShouldSuccess() {
    var book = BooksCreator.getBook();
    when(bookDao.findById(anyLong())).thenReturn(Optional.of(book));
    when(lendDao.findByBook(book)).thenReturn(Optional.empty());
    doNothing().when(bookDao).delete(book);
    bookService.delete(1L);
  }

  @Test
  void deleteNonExistentBookShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> bookService.delete(1L));
  }

  @Test
  void deleteBookInUseShouldThrowCurrentlyInUseException() {
    var book = BooksCreator.getBook();

    when(bookDao.findById(anyLong())).thenReturn(Optional.of(book));
    when(lendDao.findByBook(book)).thenReturn(Optional.of(new Lend()));

    assertThrows(CurrentlyInUseException.class, () -> bookService.delete(1L));
  }

  @Test
  void findOneShouldSuccess() {
    var book = BooksCreator.getBook();

    when(bookDao.findById(anyLong())).thenReturn(Optional.of(book));
    BookResponseDto bookDto = bookService.findOne(1L);

    assertEquals(book.getId(), bookDto.getId());
    assertEquals(book.getName(), bookDto.getName());
    assertEquals(book.getAuthor(), bookDto.getAuthor());
    assertEquals(book.getIsbn(), bookDto.getIsbn());
    assertEquals(book.getYear(), bookDto.getYear());
  }

  @Test
  void findOneNonExistentBookShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> bookService.findOne(1L));
  }

  @Test
  void findAllShouldSuccess() {
    List<Book> books = BooksCreator.getBooks();
    Page<Book> page = new PageImpl<>(books);

    Pageable pageable = Pageable.unpaged();
    SpecificationsBuilder<Book> builder = new SpecificationsBuilder<>();
    Specification<Book> spec = builder.build();
    when(bookDao.findAll(spec, pageable)).thenReturn(page);
    Page<BookResponseDto> bookDtos = bookService.findAll(null, pageable);
    assertEquals(10, bookDtos.getContent().size());

    BookResponseDto bookDto = bookDtos.iterator().next();
    Book book = books.get(0);
    assertEquals(book.getId(), bookDto.getId());
    assertEquals(book.getName(), bookDto.getName());
    assertEquals(book.getAuthor(), bookDto.getAuthor());
    assertEquals(book.getIsbn(), bookDto.getIsbn());
    assertEquals(book.getYear(), bookDto.getYear());
  }

  @Test
  public void exportShouldSuccess() throws IOException {
    when(bookDao.findAll()).thenReturn(BooksCreator.getBooks());
    when(exporterFactory.newInstance("csv")).thenReturn(new CsvFileExporter());
    byte[] export = bookService.export("csv");
    assertNotNull(export);

    String asString = new String(export, "UTF-8");
    assertTrue(asString.contains("author,created,id,isbn,name,updated,year"));
    assertTrue(asString.contains("author,,,1645712740,name,,1999"));
  }
}
