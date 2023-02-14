package com.naskoni.library.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.ClientService;
import com.naskoni.library.util.ClientsCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClientControllerTest {

  private static final String CLIENTS_URI = "/clients";
  private static final String CLIENTS_URI_WITH_PARAM = "/clients/1";
  private static final String RESPONSE =
      "{\"id\":1,\"created\":null,\"updated\":null,\"name\":\"Max Max\",\"pid\":\"1645712740\",\"birthdate\":\"2019-12-31\",\"createdBy\":\"admin\"}";

  private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
  private MockMvc mockMvc;
  @Mock private ClientService clientService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ClientController clientController = new ClientController(clientService);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(clientController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(ClientsCreator.getClientRequestDto());

    when(clientService.create(any())).thenReturn(ClientsCreator.getClientResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(CLIENTS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(clientService).create(any());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new ClientRequestDto());

    mockMvc
        .perform(
            post(CLIENTS_URI)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    verify(clientService, times(0)).create(any());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(ClientsCreator.getClientRequestDto());

    when(clientService.update(anyLong(), any())).thenReturn(ClientsCreator.getClientResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(CLIENTS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(clientService).update(any(), any());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new ClientRequestDto());

    mockMvc
        .perform(
            put(CLIENTS_URI_WITH_PARAM)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    verify(clientService, times(0)).update(any(), any());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void updateNonExistentClientShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(ClientsCreator.getClientResponseDto());

    doThrow(new NotFoundException("")).when(clientService).update(anyLong(), any());

    mockMvc
        .perform(
            put(CLIENTS_URI_WITH_PARAM)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

    verify(clientService).update(anyLong(), any());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void deleteExistentClientShouldReturnHttpNoContent() throws Exception {
    doNothing().when(clientService).delete(anyLong());
    mockMvc.perform(delete(CLIENTS_URI_WITH_PARAM)).andExpect(status().isNoContent());
  }

  @Test
  void deleteNonExistentClientShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(clientService).delete(anyLong());

    mockMvc
        .perform(delete(CLIENTS_URI_WITH_PARAM))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

    verify(clientService).delete(anyLong());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void deleteClientInUseShouldFailHttpConflict() throws Exception {
    doThrow(new CurrentlyInUseException("")).when(clientService).delete(anyLong());

    mockMvc
        .perform(delete(CLIENTS_URI_WITH_PARAM))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof CurrentlyInUseException));

    verify(clientService).delete(anyLong());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void findOneShouldReturnHttpOk() throws Exception {
    when(clientService.findOne(any())).thenReturn(ClientsCreator.getClientResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(get(CLIENTS_URI_WITH_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(clientService).findOne(anyLong());
    verifyNoMoreInteractions(clientService);
  }

  @Test
  void findAllShouldReturnHttpOk() throws Exception {
    when(clientService.findAll(any(), any()))
        .thenReturn(new PageImpl<>(ClientsCreator.getClientResponseDtos()));

    MvcResult mvcResult =
        mockMvc
            .perform(get(CLIENTS_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertTrue(content.contains(RESPONSE));
    assertTrue(content.contains("\"totalElements\":10"));

    verify(clientService).findAll(any(), any());
    verifyNoMoreInteractions(clientService);
  }
}
