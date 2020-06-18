package com.naskoni.library.service.impl;

import com.naskoni.library.dao.ClientDao;
import com.naskoni.library.dao.LendDao;
import com.naskoni.library.dao.UserDao;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.Lend;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.specification.SpecificationsBuilder;
import com.naskoni.library.util.BooksCreator;
import com.naskoni.library.util.ClientsCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ClientServiceTest {

  @Mock private ClientDao clientDao;
  @Mock private LendDao lendDao;
  @Mock private UserDao userDao;
  @Mock private AuthenticationFacade authenticationFacade;

  @InjectMocks private ClientServiceImpl clientService;

  @Test
  void createShouldSuccess() {
    var clientRequestDto = ClientsCreator.getClientRequestDto();
    Client client = clientService.mapToEntity(clientRequestDto);

    Authentication authentication = mock(Authentication.class);
    when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("user");
    when(clientDao.save(client)).thenReturn(client);
    ClientResponseDto clientResponseDto = clientService.create(clientRequestDto);

    assertEquals(client.getId(), clientResponseDto.getId());
    assertEquals(client.getName(), clientResponseDto.getName());
    assertEquals(client.getBirthdate(), clientResponseDto.getBirthdate());
    assertEquals(client.getPid(), clientResponseDto.getPid());
  }

  @Test
  void updateExistentClientShouldSuccess() {
    var clientRequestDto = ClientsCreator.getClientRequestDto();
    Client client = clientService.mapToEntity(clientRequestDto);

    when(clientDao.findById(anyLong())).thenReturn(Optional.of(client));
    when(clientDao.save(client)).thenReturn(client);
    ClientResponseDto clientResponseDto = clientService.update(1L, clientRequestDto);

    assertEquals(client.getId(), clientResponseDto.getId());
    assertEquals(client.getName(), clientResponseDto.getName());
    assertEquals(client.getBirthdate(), clientResponseDto.getBirthdate());
    assertEquals(client.getPid(), clientResponseDto.getPid());
  }

  @Test
  void updateNonExistentClientShouldThrowNotFoundException() {
    var clientRequestDto = ClientsCreator.getClientRequestDto();
    assertThrows(NotFoundException.class, () -> clientService.update(1L, clientRequestDto));
  }

  @Test
  void deleteExistentClientShouldSuccess() {
    var client = ClientsCreator.getClient();
    when(clientDao.findById(anyLong())).thenReturn(Optional.of(client));
    when(lendDao.findByClient(client)).thenReturn(Optional.empty());
    doNothing().when(clientDao).delete(client);
    clientService.delete(1L);
  }

  @Test
  void deleteNonExistentClientShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> clientService.delete(1L));
  }

  @Test
  void deleteClientInUseShouldThrowCurrentlyInUseException() {
    var client = ClientsCreator.getClient();

    when(clientDao.findById(anyLong())).thenReturn(Optional.of(client));
    when(lendDao.findByClient(client)).thenReturn(Optional.of(new Lend()));

    assertThrows(CurrentlyInUseException.class, () -> clientService.delete(1L));
  }

  @Test
  void findOneShouldSuccess() {
    var client = ClientsCreator.getClient();

    when(clientDao.findById(anyLong())).thenReturn(Optional.of(client));
    ClientResponseDto clientDto = clientService.findOne(1L);

    assertEquals(client.getId(), clientDto.getId());
    assertEquals(client.getName(), clientDto.getName());
    assertEquals(client.getBirthdate(), clientDto.getBirthdate());
    assertEquals(client.getPid(), clientDto.getPid());
  }

  @Test
  void findOneNonExistentClientShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> clientService.findOne(1L));
  }

  @Test
  void findAllShouldSuccess() {
    List<Client> clients = ClientsCreator.getClients();
    Page<Client> page = new PageImpl<>(clients);

    Pageable pageable = Pageable.unpaged();
    SpecificationsBuilder<Client> builder = new SpecificationsBuilder<>();
    Specification<Client> spec = builder.build();
    Mockito.when(clientDao.findAll(spec, pageable)).thenReturn(page);
    Page<ClientResponseDto> clientDtos = clientService.findAll(null, pageable);
    assertEquals(10, clientDtos.getContent().size());

    ClientResponseDto clientDto = clientDtos.iterator().next();
    Client client = clients.get(0);
    assertEquals(client.getId(), clientDto.getId());
    assertEquals(client.getName(), clientDto.getName());
    assertEquals(client.getBirthdate(), clientDto.getBirthdate());
    assertEquals(client.getPid(), clientDto.getPid());
  }
}
