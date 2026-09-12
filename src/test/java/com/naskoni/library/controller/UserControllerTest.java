package com.naskoni.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.exception.DuplicateException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.UserService;
import com.naskoni.library.util.UsersCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

class UserControllerTest {

  private static final String USERS_URI = "/users";

  private static final String RESPONSE = """
      {
        "id": 1,
        "created": null,
        "updated": null,
        "name": "name",
        "username": "user",
        "status": "ACTIVE",
        "role": "ROLE_USER"
      }
      """;

  private static final String USERS_URI_WITH_PARAM = "/users/1";

  private final Gson gson = new Gson();

  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    UserController userController = new UserController(userService);

    this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .setControllerAdvice(new ErrorHandler())
        .build();
  }

  @Test
  void createWithValidDtoShouldReturnHttpCreated() throws Exception {

    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.create(any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult = mockMvc.perform(post(USERS_URI)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()).andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(userService).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {

    String json = gson.toJson(new UserRequestDto());

    mockMvc.perform(post(USERS_URI).content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

    verify(userService, times(0)).create(any());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithExistentUsernameShouldFailHttpConflict() throws Exception {

    doThrow(new DuplicateException("")).when(userService).create(any());

    String json = gson.toJson(UsersCreator.getUserRequestDto());

    mockMvc.perform(post(USERS_URI).content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(result -> assertInstanceOf(DuplicateException.class, result.getResolvedException()));

    verify(userService).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithValidDtoShouldReturnHttpOk() throws Exception {

    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.update(anyLong(), any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult = mockMvc.perform(put(USERS_URI_WITH_PARAM)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(userService).update(anyLong(), any());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {

    String json = gson.toJson(new UserRequestDto());

    mockMvc.perform(put(USERS_URI_WITH_PARAM).content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

    verify(userService, times(0)).update(anyLong(), any());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateNonExistentUserShouldFailHttpNotFound() throws Exception {

    String json = gson.toJson(UsersCreator.getUserRequestDto());

    doThrow(new NotFoundException("")).when(userService).update(anyLong(), any());

    mockMvc.perform(put(USERS_URI_WITH_PARAM)
            .content(json).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));

    verify(userService).update(anyLong(), any());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void deactivateExistentUserShouldReturnHttpOk() throws Exception {

    when(userService.deactivate(anyLong())).thenReturn(UsersCreator.getUserResponseDto());

    mockMvc.perform(patch(USERS_URI_WITH_PARAM))
        .andExpect(status().isOk());

    verify(userService).deactivate(anyLong());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void deactivateNonExistentUserShouldFailHttpNotFound() throws Exception {

    doThrow(new NotFoundException("")).when(userService).deactivate(anyLong());

    mockMvc.perform(patch(USERS_URI_WITH_PARAM))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));

    verify(userService).deactivate(anyLong());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void findOneShouldReturnHttpOk() throws Exception {

    when(userService.findOne(anyLong())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult = mockMvc.perform(get(USERS_URI_WITH_PARAM)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(userService).findOne(anyLong());

    verifyNoMoreInteractions(userService);
  }

  @Test
  void findAllShouldReturnHttpOk() throws Exception {

    var users = UsersCreator.getUserResponseDtos();

    when(userService.findAll(any(), any()))
        .thenReturn(new PageImpl<>(users, PageRequest.of(0, 10), users.size()));

    MvcResult mvcResult = mockMvc.perform(get(USERS_URI)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);

    JsonNode json = objectMapper.readTree(content);

    assertTrue(content.contains("\"id\":1"));

    assertTrue(content.contains("\"name\":\"name\""));

    assertTrue(content.contains("\"username\":\"user\""));

    assertTrue(content.contains("\"status\":\"ACTIVE\""));

    assertTrue(content.contains("\"role\":\"ROLE_USER\""));

    assertEquals(10, json.get("size").asInt());

    assertEquals(0, json.get("number").asInt());

    assertEquals(users.size(), json.get("totalElements").asInt());

    assertEquals((users.size() + 9) / 10, json.get("totalPages").asInt());

    assertEquals(users.size(), json.get("numberOfElements").asInt());

    verify(userService).findAll(any(), any());

    verifyNoMoreInteractions(userService);
  }

  private void assertJsonEquals(String expected, String actual) throws Exception {

    assertEquals(objectMapper.readTree(expected), objectMapper.readTree(actual));
  }
}