package com.naskoni.library.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book")
public class Book extends AbstractEntity {

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 50)
  private String author;

  private Integer year;

  @Column(length = 13)
  private String isbn;
}
