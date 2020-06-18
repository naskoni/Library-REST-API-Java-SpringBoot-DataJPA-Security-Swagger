package com.naskoni.library.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.LendService;
import com.naskoni.library.util.BooksCreator;
import com.naskoni.library.util.ClientsCreator;
import com.naskoni.library.util.LendsCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LendControllerTest {

  private static final String LENDS_URI = "/lends";
  private static final String LENDS_URI_WITH_PARAM = "/lends/1";
  private static final String RESPONSE =
      "{\"id\":1,\"created\":null,\"updated\":null,\"book\":{\"id\":1,\"created\":null,\"updated\":null,\"name\":\"Don Quixote\",\"author\":\"Miguel de Cervantes\",\"year\":1999,\"isbn\":\"1645712740\"},\"client\":{\"id\":1,\"created\":null,\"updated\":null,\"name\":\"Max Max\",\"pid\":\"1645712740\",\"birthdate\":\"2019-12-31\",\"createdBy\":\"admin\"},\"lendingDate\":\"2019-12-31\",\"returnDate\":\"2020-01-01\"}";

  private MockMvc mockMvc;

  @InjectMocks private LendController clientController;

  @Mock private LendService lendService;

  private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(clientController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  public void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(LendsCreator.getLendRequestDto());

    when(lendService.create(any())).thenReturn(LendsCreator.getLendResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(LENDS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(lendService, times(1)).create(any());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new BookRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(LENDS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class);
    verify(lendService, times(0)).create(any());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(LendsCreator.getLendRequestDto());

    when(lendService.update(any(), any())).thenReturn(LendsCreator.getLendResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(LENDS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(lendService, times(1)).update(any(), any());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new LendRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(LENDS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertTrue(
        mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));
    verify(lendService, times(0)).update(any(), any());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void updateNonExistentLendShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(LendsCreator.getLendRequestDto());

    doThrow(new NotFoundException("")).when(lendService).update(anyLong(), any());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(LENDS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertTrue(mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    verify(lendService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void findOneShouldReturnHttpOk() throws Exception {
    when(lendService.findOne(anyLong())).thenReturn(LendsCreator.getLendResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(get(LENDS_URI_WITH_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(lendService, times(1)).findOne(anyLong());
    verifyNoMoreInteractions(lendService);
  }

  @Test
  public void testFindAll() throws Exception {
    when(lendService.findAll(any(), any())).thenReturn(new PageImpl<>(LendsCreator.getLendResponseDtos()));

    MvcResult mvcResult =
        mockMvc
            .perform(get(LENDS_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.contains(RESPONSE));
    assertTrue(content.contains("\"totalElements\":10"));
    verify(lendService, times(1)).findAll(any(), any());
    verifyNoMoreInteractions(lendService);
  }


}
