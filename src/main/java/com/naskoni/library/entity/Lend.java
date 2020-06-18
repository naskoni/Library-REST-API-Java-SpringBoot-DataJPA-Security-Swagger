package com.naskoni.library.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lend")
public class Lend extends AbstractEntity {

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(name = "lending_date")
  private Date lendingDate;

  @Column(name = "return_date")
  private Date returnDate;
}
