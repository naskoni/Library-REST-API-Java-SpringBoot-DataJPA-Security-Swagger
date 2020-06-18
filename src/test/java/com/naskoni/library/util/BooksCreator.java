package com.naskoni.library.util;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.entity.Book;

import java.util.ArrayList;
import java.util.List;

public class BooksCreator {

  public static Book getBook() {
    var book = new Book();
    book.setName("name");
    book.setAuthor("author");
    book.setIsbn("1645712740");
    book.setYear(1999);

    return book;
  }

  public static List<Book> getBooks() {
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      books.add(getBook());
    }

    return books;
  }

  public static BookResponseDto getBookResponseDto() {
    var book = new BookResponseDto();
    book.setId(1L);
    book.setName("Don Quixote");
    book.setAuthor("Miguel de Cervantes");
    book.setIsbn("1645712740");
    book.setYear(1999);
    return book;
  }

  public static BookRequestDto getBookRequestDto() {
    var book = new BookRequestDto();
    book.setName("Don Quixote");
    book.setAuthor("Miguel de Cervantes");
    book.setIsbn("1645712740");
    book.setYear(1999);
    return book;
  }

  public static List<BookResponseDto> getBookResponseDtos() {
    List<BookResponseDto> books = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      books.add(getBookResponseDto());
    }

    return books;
  }
}
