package com.naskoni.library.service.impl;

import com.naskoni.library.repository.BookRepository;
import com.naskoni.library.repository.ClientRepository;
import com.naskoni.library.repository.LendRepository;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.entity.Book;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.LendService;
import com.naskoni.library.specification.SpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;

import static com.naskoni.library.service.impl.BookServiceImpl.BOOK_NOT_FOUND;
import static com.naskoni.library.service.impl.ClientServiceImpl.CLIENT_NOT_FOUND;

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
    Lend lend = mapToEntity(lendRequestDto);
    Lend savedLend = lendRepository.save(lend);
    return mapToDto(savedLend);
  }

  @Transactional
  @Override
  public LendResponseDto update(Long id, LendRequestDto lendRequestDto) {
    Optional<Lend> optionalLend = lendRepository.findById(id);
    if (optionalLend.isPresent()) {
      Lend lend = optionalLend.get();
      BeanUtils.copyProperties(lendRequestDto, lend);
      Lend savedLend = lendRepository.save(lend);
      return mapToDto(savedLend);
    } else {
      throw new NotFoundException(String.format(LEND_NOT_FOUND, id));
    }
  }

  @Transactional(readOnly = true)
  @Override
  public LendResponseDto findOne(Long id) {
    Optional<Lend> optionalLend = lendRepository.findById(id);
    if (optionalLend.isPresent()) {
      return mapToDto(optionalLend.get());
    } else {
      throw new NotFoundException(String.format(LEND_NOT_FOUND, id));
    }
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

    Optional<Book> optionalBook = bookRepository.findById(lendRequestDto.getBookId());
    if (optionalBook.isPresent()) {
      lend.setBook(optionalBook.get());
    } else {
      throw new NotFoundException(String.format(BOOK_NOT_FOUND, lendRequestDto.getBookId()));
    }

    Optional<Client> optionalClient = clientRepository.findById(lendRequestDto.getClientId());
    if (optionalClient.isPresent()) {
      lend.setClient(optionalClient.get());
    } else {
      throw new NotFoundException(String.format(CLIENT_NOT_FOUND, lendRequestDto.getClientId()));
    }

    return lend;
  }
}
