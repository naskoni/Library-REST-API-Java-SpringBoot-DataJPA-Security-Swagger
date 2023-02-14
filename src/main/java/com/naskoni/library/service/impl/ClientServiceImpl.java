package com.naskoni.library.service.impl;

import com.naskoni.library.dao.ClientDao;
import com.naskoni.library.dao.LendDao;
import com.naskoni.library.dao.UserDao;
import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.entity.User;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.service.ClientService;
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

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  public static final String CLIENT_NOT_FOUND = "Client with id: %d could not be found";
  public static final String CLIENT_IN_USE = "Client with id: %d is currently in use";

  private final ClientDao clientDao;
  private final LendDao lendDao;
  private final UserDao userDao;
  private final AuthenticationFacade authenticationFacade;

  @Override
  @Transactional
  public ClientResponseDto create(ClientRequestDto clientDto) {
    Client client = mapToEntity(clientDto);
    String username = authenticationFacade.getAuthentication().getName();
    Optional<User> userOptional = userDao.findByUsername(username);
    if (userOptional.isPresent()) {
      client.setCreatedBy(userOptional.get());
    }

    Client savedClient = clientDao.save(client);

    return mapToDto(savedClient);
  }

  @Override
  @Transactional
  public ClientResponseDto update(Long id, ClientRequestDto clientDto) {
    Optional<Client> optionalClient = clientDao.findById(id);
    if (optionalClient.isPresent()) {
      Client client = optionalClient.get();
      BeanUtils.copyProperties(clientDto, client);
      Client savedClient = clientDao.save(client);
      return mapToDto(savedClient);
    } else {
      throw new NotFoundException(String.format(CLIENT_NOT_FOUND, id));
    }
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Optional<Client> optionalClient = clientDao.findById(id);
    if (optionalClient.isPresent()) {
      Client client = optionalClient.get();
      Optional<Lend> lendOptional = lendDao.findByClient(client);
      if (lendOptional.isPresent()) {
        throw new CurrentlyInUseException(String.format(CLIENT_IN_USE, id));
      }

      clientDao.delete(optionalClient.get());
    } else {
      throw new NotFoundException(String.format(CLIENT_NOT_FOUND, id));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ClientResponseDto findOne(Long id) {
    Optional<Client> optionalClient = clientDao.findById(id);
    if (optionalClient.isPresent()) {
      return mapToDto(optionalClient.get());
    } else {
      throw new NotFoundException(String.format(CLIENT_NOT_FOUND, id));
    }
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
    Page<Client> clients = clientDao.findAll(spec, pageable);
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
