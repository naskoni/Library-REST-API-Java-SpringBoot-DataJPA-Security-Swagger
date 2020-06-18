package com.naskoni.library.controller;

import com.google.gson.Gson;
import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.FileTypesDto;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.service.BookService;
import com.naskoni.library.util.BooksCreator;
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

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

  private static final String BOOKS_URI_WITH_PARAM = "/books/1";
  private static final String BOOKS_URI = "/books";
  private static final String RESPONSE =
      "{\"id\":1,\"created\":null,\"updated\":null,\"name\":\"Don Quixote\",\"author\":\"Miguel de Cervantes\",\"year\":1999,\"isbn\":\"1645712740\"}";
  private MockMvc mockMvc;

  @InjectMocks private BookController bookController;

  @Mock private BookService bookService;

  private Gson gson = new Gson();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(bookController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  public void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(BooksCreator.getBookRequestDto());

    when(bookService.create(any())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(BOOKS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(bookService, times(1)).create(any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new BookRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(BOOKS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class);
    verify(bookService, times(0)).create(any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(BooksCreator.getBookResponseDto());

    when(bookService.update(anyLong(), any())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(BOOKS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(bookService, times(1)).update(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new BookRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(BOOKS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertTrue(
        mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));
    verify(bookService, times(0)).update(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void updateNonExistentBookShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(BooksCreator.getBookResponseDto());

    doThrow(new NotFoundException("")).when(bookService).update(anyLong(), any());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(BOOKS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertTrue(mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    verify(bookService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void deleteExistentBookShouldReturnHttpNoContent() throws Exception {
    doNothing().when(bookService).delete(anyLong());
    mockMvc.perform(delete(BOOKS_URI_WITH_PARAM)).andExpect(status().isNoContent());
  }

  @Test
  public void deleteNonExistentBookShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(bookService).delete(anyLong());

    MvcResult mvcResult =
        mockMvc.perform(delete(BOOKS_URI_WITH_PARAM)).andExpect(status().isNotFound()).andReturn();
    assertTrue(mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    verify(bookService, times(1)).delete(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void deleteBookInUseShouldFailHttpConflict() throws Exception {
    doThrow(new CurrentlyInUseException("")).when(bookService).delete(anyLong());

    MvcResult mvcResult =
        mockMvc.perform(delete(BOOKS_URI_WITH_PARAM)).andExpect(status().isConflict()).andReturn();
    assertTrue(mvcResult.getResolvedException().getClass().equals(CurrentlyInUseException.class));
    verify(bookService, times(1)).delete(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void findOneShouldReturnHttpOk() throws Exception {
    when(bookService.findOne(anyLong())).thenReturn(BooksCreator.getBookResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(get(BOOKS_URI_WITH_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals(RESPONSE));
    verify(bookService, times(1)).findOne(anyLong());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void findAllShouldReturnHttpOk() throws Exception {
    when(bookService.findAll(any(), any()))
        .thenReturn(new PageImpl<>(BooksCreator.getBookResponseDtos()));

    MvcResult mvcResult =
        mockMvc
            .perform(get(BOOKS_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.contains(RESPONSE));
    assertTrue(content.contains("\"totalElements\":10"));
    verify(bookService, times(1)).findAll(any(), any());
    verifyNoMoreInteractions(bookService);
  }

  @Test
  public void retrieveSupportedFileTypesShouldReturnHttpOk() throws Exception {
    String uri = "/books/files";

    var fileTypes = new LinkedHashSet<String>();
    fileTypes.add("csv");
    fileTypes.add("xls");
    FileTypesDto dto = new FileTypesDto();
    dto.setSupportedFileTypes(fileTypes);
    when(bookService.retrieveSupportedFileTypes()).thenReturn((dto));

    MvcResult mvcResult =
        mockMvc
            .perform(get(uri).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.equals("{\"supportedFileTypes\":[\"csv\",\"xls\"]}"));
    verify(bookService, times(1)).retrieveSupportedFileTypes();
    verifyNoMoreInteractions(bookService);
  }
}
