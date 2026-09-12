package com.naskoni.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.FileTypesDto;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.BookService;
import com.naskoni.library.util.BooksCreator;
import java.util.LinkedHashSet;
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

class BookControllerTest {

  private static final String BOOKS_URI_WITH_PARAM = "/books/1";
  private static final String BOOKS_URI = "/books";

  private static final String RESPONSE = """
      {
        "id":1,
        "created":null,
        "updated":null,
        "name":"Don Quixote",
        "author":"Miguel de Cervantes",
        "year":1999,
        "isbn":"1645712740"
      }
      """;

  private static final String SUPPORTED_FILE_TYPES = "{\"supportedFileTypes\":[\"csv\",\"xls\"]}";

  private final Gson gson = new Gson();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mockMvc;

  @Mock
  private BookService bookService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    BookController bookController = new BookController(bookService);

    this.mockMvc = MockMvcBuilders.standaloneSetup(bookController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .setControllerAdvice(new ErrorHandler())
        .build();
  }

  @Test
  void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(BooksCreator.getBookRequestDto());

    when(bookService.create(any())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult = mockMvc.perform(post(BOOKS_URI)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()).andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(bookService).create(any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new BookRequestDto());

    mockMvc.perform(post(BOOKS_URI).content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

    verify(bookService, times(0)).create(any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(BooksCreator.getBookResponseDto());

    when(bookService.update(anyLong(), any())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult = mockMvc.perform(put(BOOKS_URI_WITH_PARAM)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON).
            accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(bookService).update(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new BookRequestDto());

    mockMvc.perform(put(BOOKS_URI_WITH_PARAM)
            .content(json).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

    verify(bookService, times(0)).update(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void updateNonExistentBookShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(BooksCreator.getBookResponseDto());

    doThrow(new NotFoundException("")).when(bookService).update(anyLong(), any());

    mockMvc.perform(put(BOOKS_URI_WITH_PARAM).content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));

    verify(bookService).update(anyLong(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void deleteExistentBookShouldReturnHttpNoContent() throws Exception {
    doNothing().when(bookService).delete(anyLong());

    mockMvc.perform(delete(BOOKS_URI_WITH_PARAM))
        .andExpect(status()
            .isNoContent());
  }

  @Test
  void deleteNonExistentBookShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(bookService).delete(anyLong());

    mockMvc.perform(delete(BOOKS_URI_WITH_PARAM))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));

    verify(bookService).delete(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void deleteBookInUseShouldFailHttpConflict() throws Exception {
    doThrow(new CurrentlyInUseException("")).when(bookService).delete(anyLong());

    mockMvc.perform(delete(BOOKS_URI_WITH_PARAM))
        .andExpect(status().isConflict())
        .andExpect(result -> assertInstanceOf(CurrentlyInUseException.class, result.getResolvedException()));

    verify(bookService).delete(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void findOneShouldReturnHttpOk() throws Exception {
    when(bookService.findOne(anyLong())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult = mockMvc.perform(get(BOOKS_URI_WITH_PARAM)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(RESPONSE, content);

    verify(bookService).findOne(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void findAllShouldReturnHttpOk() throws Exception {
    when(bookService.findAll(any(), any()))
        .thenReturn(new PageImpl<>(BooksCreator.getBookResponseDtos(),
            PageRequest.of(0, 10), BooksCreator.getBookResponseDtos()
            .size()));

    MvcResult mvcResult = mockMvc.perform(get(BOOKS_URI).accept(MediaType.APPLICATION_JSON)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);

    assertTrue(content.contains("\"id\":1"));
    assertTrue(content.contains("\"name\":\"Don Quixote\""));
    assertTrue(content.contains("\"author\":\"Miguel de Cervantes\""));
    assertTrue(content.contains("\"year\":1999"));
    assertTrue(content.contains("\"isbn\":\"1645712740\""));
    assertTrue(content.contains("\"totalElements\":10"));

    verify(bookService).findAll(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void retrieveSupportedFileTypesShouldReturnHttpOk() throws Exception {
    String uri = "/books/files";

    var fileTypes = new LinkedHashSet<String>();
    fileTypes.add("csv");
    fileTypes.add("xls");

    FileTypesDto dto = new FileTypesDto();
    dto.setSupportedFileTypes(fileTypes);

    when(bookService.retrieveSupportedFileTypes()).thenReturn(dto);

    MvcResult mvcResult = mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();

    assertNotNull(content);
    assertJsonEquals(SUPPORTED_FILE_TYPES, content);

    verify(bookService).retrieveSupportedFileTypes();
    verifyNoMoreInteractions(bookService);
  }

  private void assertJsonEquals(String expected, String actual) throws Exception {
    assertEquals(objectMapper.readTree(expected), objectMapper.readTree(actual));
  }
}
