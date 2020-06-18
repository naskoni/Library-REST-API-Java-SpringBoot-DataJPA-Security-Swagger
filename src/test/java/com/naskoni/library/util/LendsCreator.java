package com.naskoni.library.util;

import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.entity.Lend;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class LendsCreator {

  public static Lend getLend() {
    var lend = new Lend();
    lend.setId(1L);
    lend.setClient(ClientsCreator.getClient());
    lend.setBook(BooksCreator.getBook());
    lend.setLendingDate(new Date(System.currentTimeMillis()));
    lend.setReturnDate(new Date(System.currentTimeMillis()));
    return lend;
  }

  public static List<Lend> getLends() {
    List<Lend> lends = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      lends.add(getLend());
    }

    return lends;
  }

  public static LendRequestDto getLendRequestDto() {
    var lend = new LendRequestDto();
    lend.setBookId(1L);
    lend.setClientId(1L);
    lend.setLendingDate(Date.valueOf("2020-01-01"));
    lend.setReturnDate(Date.valueOf("2020-01-02"));
    return lend;
  }

  public static LendResponseDto getLendResponseDto() {
    var lend = new LendResponseDto();
    lend.setId(1L);
    lend.setBook(BooksCreator.getBookResponseDto());
    lend.setClient(ClientsCreator.getClientResponseDto());
    lend.setLendingDate(Date.valueOf("2020-01-01"));
    lend.setReturnDate(Date.valueOf("2020-01-02"));
    return lend;
  }

  public static List<LendResponseDto> getLendResponseDtos() {
    List<LendResponseDto> lends = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      lends.add(getLendResponseDto());
    }

    return lends;
  }
}
