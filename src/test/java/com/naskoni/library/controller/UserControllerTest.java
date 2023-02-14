package com.naskoni.library.controller;

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

class UserControllerTest {

  static final String USERS_URI = "/users";
  static final String RESPONSE =
      "{\"id\":1,\"created\":null,\"updated\":null,\"name\":\"name\",\"username\":\"user\",\"status\":\"ACTIVE\",\"role\":\"ROLE_USER\"}";
  static final String USERS_URI_WITH_PARAM = "/users/1";

  private final Gson gson = new Gson();
  private MockMvc mockMvc;
  @Mock private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    UserController userController = new UserController(userService);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.create(any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(USERS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(userService, times(1)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new UserRequestDto());

    mockMvc
        .perform(
            post(USERS_URI)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    verify(userService, times(0)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithExistentUsernameShouldFailHttpConflict() throws Exception {
    doThrow(new DuplicateException("")).when(userService).create(any());
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    mockMvc
        .perform(
            post(USERS_URI)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateException));

    verify(userService, times(1)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.update(anyLong(), any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(USERS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(userService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new UserRequestDto());

    mockMvc
        .perform(
            put(USERS_URI_WITH_PARAM)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    verify(userService, times(0)).update(any(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateNonExistentUserShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    doThrow(new NotFoundException("")).when(userService).update(anyLong(), any());

    mockMvc
        .perform(
            put(USERS_URI_WITH_PARAM)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

    verify(userService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void deactivateExistentUserShouldReturnHttpOk() throws Exception {
    when(userService.deactivate(anyLong())).thenReturn(UsersCreator.getUserResponseDto());
    mockMvc.perform(patch(USERS_URI_WITH_PARAM)).andExpect(status().isOk());
  }

  @Test
  void deactivateNonExistentUserShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(userService).deactivate(anyLong());

    mockMvc.perform(patch(USERS_URI_WITH_PARAM))
          .andExpect(status().isNotFound())
          .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

    verify(userService, times(1)).deactivate(anyLong());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void findOneShouldReturnHttpOk() throws Exception {
    when(userService.findOne(anyLong())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(get(USERS_URI_WITH_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertEquals(RESPONSE, content);

    verify(userService, times(1)).findOne(anyLong());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void findAllShouldReturnHttpOk() throws Exception {
    when(userService.findAll(any(), any())).thenReturn(new PageImpl<>(UsersCreator.getUserResponseDtos()));

    MvcResult mvcResult =
        mockMvc
            .perform(get(USERS_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertTrue(content.contains(RESPONSE));
    assertTrue(content.contains("\"totalElements\":10"));

    verify(userService, times(1)).findAll(any(), any());
    verifyNoMoreInteractions(userService);
  }


}
