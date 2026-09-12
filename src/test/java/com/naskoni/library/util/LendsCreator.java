package com.naskoni.library.util;

import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.entity.Lend;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LendsCreator {

  public static Lend getLend() {
    var lend = new Lend();
    lend.setId(1L);
    lend.setClient(ClientsCreator.getClient());
    lend.setBook(BooksCreator.getBook());
    lend.setLendingDate(LocalDate.of(2019, 12, 31));
    lend.setReturnDate(LocalDate.of(2020, 1, 1));
    return lend;
  }

  public static LendRequestDto getLendRequestDto() {
    var lend = new LendRequestDto();
    lend.setBookId(1L);
    lend.setClientId(1L);
    lend.setLendingDate(LocalDate.of(2019, 12, 31));
    lend.setReturnDate(LocalDate.of(2020, 1, 1));
    return lend;
  }

  public static LendResponseDto getLendResponseDto() {
    var lend = new LendResponseDto();
    lend.setId(1L);
    lend.setBook(BooksCreator.getBookResponseDto());
    lend.setClient(ClientsCreator.getClientResponseDto());
    lend.setLendingDate(LocalDate.of(2019, 12, 31));
    lend.setReturnDate(LocalDate.of(2020, 1, 1));
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