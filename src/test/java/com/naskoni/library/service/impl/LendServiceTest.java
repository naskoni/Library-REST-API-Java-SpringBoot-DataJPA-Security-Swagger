package com.naskoni.library.service.impl;

import com.naskoni.library.dao.BookDao;
import com.naskoni.library.dao.ClientDao;
import com.naskoni.library.dao.LendDao;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.specification.SpecificationsBuilder;
import com.naskoni.library.util.BooksCreator;
import com.naskoni.library.util.ClientsCreator;
import com.naskoni.library.util.LendsCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LendServiceTest {

  @Mock private LendDao lendDao;
  @Mock private BookDao bookDao;
  @Mock private ClientDao clientDao;

  @InjectMocks private LendServiceImpl lendService;

  @Test
  void createShouldSuccess() {
    when(bookDao.findById(anyLong())).thenReturn(Optional.of(BooksCreator.getBook()));
    when(clientDao.findById(anyLong())).thenReturn(Optional.of(ClientsCreator.getClient()));

    var lendRequestDto = LendsCreator.getLendRequestDto();
    Lend lend = lendService.mapToEntity(lendRequestDto);

    when(lendDao.save(lend)).thenReturn(lend);
    LendResponseDto lendResponseDto = lendService.create(lendRequestDto);

    assertEquals(lend.getId(), lendResponseDto.getId());
    assertEquals(lend.getBook().getId(), lendResponseDto.getBook().getId());
    assertEquals(lend.getClient().getId(), lendResponseDto.getClient().getId());
    assertEquals(lend.getLendingDate(), lendResponseDto.getLendingDate());
    assertEquals(lend.getReturnDate(), lendResponseDto.getReturnDate());
  }

  @Test
  void updateExistentLendShouldSuccess() {
    when(bookDao.findById(anyLong())).thenReturn(Optional.of(BooksCreator.getBook()));
    when(clientDao.findById(anyLong())).thenReturn(Optional.of(ClientsCreator.getClient()));

    var lendRequestDto = LendsCreator.getLendRequestDto();
    Lend lend = lendService.mapToEntity(lendRequestDto);

    when(lendDao.findById(anyLong())).thenReturn(Optional.of(lend));
    when(lendDao.save(lend)).thenReturn(lend);
    LendResponseDto lendResponseDto = lendService.update(1L, lendRequestDto);

    assertEquals(lend.getId(), lendResponseDto.getId());
    assertEquals(lend.getBook().getId(), lendResponseDto.getBook().getId());
    assertEquals(lend.getClient().getId(), lendResponseDto.getClient().getId());
    assertEquals(lend.getLendingDate(), lendResponseDto.getLendingDate());
    assertEquals(lend.getReturnDate(), lendResponseDto.getReturnDate());
  }

  @Test
  void updateNonExistentLendShouldThrowNotFoundException() {
    var lendRequestDto = LendsCreator.getLendRequestDto();
    assertThrows(NotFoundException.class, () -> lendService.update(1L, lendRequestDto));
  }

  @Test
  void findOneShouldSuccess() {
    Lend lend = LendsCreator.getLend();

    when(lendDao.findById(anyLong())).thenReturn(Optional.of(lend));
    LendResponseDto lendResponseDto = lendService.findOne(1L);

    assertEquals(lend.getId(), lendResponseDto.getId());
    assertEquals(lend.getBook().getId(), lendResponseDto.getBook().getId());
    assertEquals(lend.getClient().getId(), lendResponseDto.getClient().getId());
    assertEquals(lend.getLendingDate(), lendResponseDto.getLendingDate());
    assertEquals(lend.getReturnDate(), lendResponseDto.getReturnDate());
  }

  @Test
  void findOneNonExistentLendShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> lendService.findOne(1L));
  }

  @Test
  void findAllShouldSuccess() {
    Lend lend = LendsCreator.getLend();
    List<Lend> lends = new ArrayList<>();
    lends.add(lend);
    Page<Lend> page = new PageImpl<>(lends);

    Pageable pageable = Pageable.unpaged();
    SpecificationsBuilder<Lend> builder = new SpecificationsBuilder<>();
    Specification<Lend> spec = builder.build();
    Mockito.when(lendDao.findAll(spec, pageable)).thenReturn(page);
    Page<LendResponseDto> lendResponseDtos = lendService.findAll(null, pageable);
    assertEquals(1, lendResponseDtos.getContent().size());

    LendResponseDto lendResponseDto = lendResponseDtos.iterator().next();
    assertEquals(lend.getId(), lendResponseDto.getId());
    assertEquals(lend.getBook().getId(), lendResponseDto.getBook().getId());
    assertEquals(lend.getClient().getId(), lendResponseDto.getClient().getId());
    assertEquals(lend.getLendingDate(), lendResponseDto.getLendingDate());
    assertEquals(lend.getReturnDate(), lendResponseDto.getReturnDate());
  }
}
