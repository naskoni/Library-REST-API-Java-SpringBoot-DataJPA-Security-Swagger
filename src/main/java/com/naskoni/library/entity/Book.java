package com.naskoni.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book")
public class Book extends AbstractEntity {

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 50)
  private String author;

  @Column(name = "book_year")
  private Integer year;

  @Column(length = 13)
  private String isbn;
}
