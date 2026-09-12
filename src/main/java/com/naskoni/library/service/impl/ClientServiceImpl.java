package com.naskoni.library.service.impl;

import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.entity.Client;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.repository.ClientRepository;
import com.naskoni.library.repository.LendRepository;
import com.naskoni.library.repository.UserRepository;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.service.ClientService;
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
public class ClientServiceImpl implements ClientService {

  public static final String CLIENT_NOT_FOUND = "Client with id: %d could not be found";
  public static final String CLIENT_IN_USE = "Client with id: %d is currently in use";

  private final ClientRepository clientRepository;
  private final LendRepository lendRepository;
  private final UserRepository userRepository;
  private final AuthenticationFacade authenticationFacade;

  @Override
  @Transactional
  public ClientResponseDto create(ClientRequestDto clientDto) {
    var client = mapToEntity(clientDto);
    var username = authenticationFacade.getAuthentication().getName();

    userRepository.findByUsername(username)
        .ifPresent(client::setCreatedBy);

    var savedClient = clientRepository.save(client);
    return mapToDto(savedClient);
  }

  @Override
  @Transactional
  public ClientResponseDto update(Long id, ClientRequestDto clientDto) {
    var client = clientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND.formatted(id)));

    BeanUtils.copyProperties(clientDto, client);

    var savedClient = clientRepository.save(client);
    return mapToDto(savedClient);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    var client = clientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND.formatted(id)));

    var lend = lendRepository.findByClient(client);
    if (lend.isPresent()) {
      throw new CurrentlyInUseException(CLIENT_IN_USE.formatted(id));
    }

    clientRepository.delete(client);
  }

  @Override
  @Transactional(readOnly = true)
  public ClientResponseDto findOne(Long id) {
    var client = clientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND.formatted(id)));

    return mapToDto(client);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ClientResponseDto> findAll(String search, Pageable pageable) {
    SpecificationsBuilder<Client> builder = new SpecificationsBuilder<>();
    Matcher matcher = Helper.getMatcher(search);

    while (matcher.find()) {
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    Specification<Client> spec = builder.build();
    Page<Client> clients = clientRepository.findAll(spec, pageable);

    return clients.map(this::mapToDto);
  }

  private ClientResponseDto mapToDto(Client client) {
    var clientDto = new ClientResponseDto();
    BeanUtils.copyProperties(client, clientDto);

    if (client.getCreatedBy() != null) {
      clientDto.setCreatedBy(client.getCreatedBy().getName());
    }

    return clientDto;
  }

  Client mapToEntity(ClientRequestDto clientDto) {
    var client = new Client();
    BeanUtils.copyProperties(clientDto, client);

    return client;
  }
}