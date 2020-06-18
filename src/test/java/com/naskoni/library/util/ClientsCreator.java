package com.naskoni.library.util;

import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ClientsCreator {

  public static ClientRequestDto getClientRequestDto() {
    var client = new ClientRequestDto();
    client.setName("name");
    client.setBirthdate(Date.valueOf("2020-01-01"));
    client.setPid("1645712740");
    return client;
  }

  public static ClientResponseDto getClientResponseDto() {
    var client = new ClientResponseDto();
    client.setId(1L);
    client.setName("Max Max");
    client.setBirthdate(Date.valueOf("2020-01-01"));
    client.setPid("1645712740");
    client.setCreatedBy("admin");
    return client;
  }

  public static List<ClientResponseDto> getClientResponseDtos() {
    List<ClientResponseDto> clients = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      clients.add(getClientResponseDto());
    }

    return clients;
  }

  public static Client getClient() {
    var client = new Client();
    client.setId(1L);
    client.setName("name");
    client.setBirthdate(new Date(System.currentTimeMillis()));
    client.setPid("164571274");
    client.setCreatedBy(new User());
    return client;
  }

  public static List<Client> getClients() {
    List<Client> clients = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      clients.add(getClient());
    }

    return clients;
  }
}
