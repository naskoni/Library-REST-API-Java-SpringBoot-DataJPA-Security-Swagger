package com.naskoni.library.service.impl;

import static com.naskoni.library.service.impl.BookServiceImpl.BOOK_NOT_FOUND;
import static com.naskoni.library.service.impl.ClientServiceImpl.CLIENT_NOT_FOUND;

import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.repository.BookRepository;
import com.naskoni.library.repository.ClientRepository;
import com.naskoni.library.repository.LendRepository;
import com.naskoni.library.service.LendService;
import com.naskoni.library.specification.SpecificationsBuilder;
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
public class LendServiceImpl implements LendService {

  public static final String LEND_NOT_FOUND = "Lend with id: %d could not be found";

  private final LendRepository lendRepository;
  private final BookRepository bookRepository;
  private final ClientRepository clientRepository;

  @Transactional
  @Override
  public LendResponseDto create(LendRequestDto lendRequestDto) {
    var lend = mapToEntity(lendRequestDto);
    var savedLend = lendRepository.save(lend);
    return mapToDto(savedLend);
  }

  @Transactional
  @Override
  public LendResponseDto update(Long id, LendRequestDto lendRequestDto) {
    var lend = lendRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(LEND_NOT_FOUND.formatted(id)));

    BeanUtils.copyProperties(lendRequestDto, lend);

    var savedLend = lendRepository.save(lend);
    return mapToDto(savedLend);
  }

  @Transactional(readOnly = true)
  @Override
  public LendResponseDto findOne(Long id) {
    var lend = lendRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(LEND_NOT_FOUND.formatted(id)));

    return mapToDto(lend);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<LendResponseDto> findAll(String search, Pageable pageable) {
    SpecificationsBuilder<Lend> builder = new SpecificationsBuilder<>();
    Matcher matcher = Helper.getMatcher(search);

    while (matcher.find()) {
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    Specification<Lend> spec = builder.build();
    Page<Lend> lends = lendRepository.findAll(spec, pageable);

    return lends.map(this::mapToDto);
  }

  private LendResponseDto mapToDto(Lend lend) {
    var lendDto = new LendResponseDto();
    BeanUtils.copyProperties(lend, lendDto);

    var clientDto = new ClientResponseDto();
    BeanUtils.copyProperties(lend.getClient(), clientDto);
    lendDto.setClient(clientDto);

    var bookDto = new BookResponseDto();
    BeanUtils.copyProperties(lend.getBook(), bookDto);
    lendDto.setBook(bookDto);

    return lendDto;
  }

  Lend mapToEntity(LendRequestDto lendRequestDto) {
    var lend = new Lend();
    BeanUtils.copyProperties(lendRequestDto, lend);

    var book = bookRepository.findById(lendRequestDto.getBookId())
        .orElseThrow(() -> new NotFoundException(
            BOOK_NOT_FOUND.formatted(lendRequestDto.getBookId())));
    lend.setBook(book);

    var client = clientRepository.findById(lendRequestDto.getClientId())
        .orElseThrow(() -> new NotFoundException(
            CLIENT_NOT_FOUND.formatted(lendRequestDto.getClientId())));
    lend.setClient(client);

    return lend;
  }
}