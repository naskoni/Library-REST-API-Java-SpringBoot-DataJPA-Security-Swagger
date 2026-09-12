package com.naskoni.library.controller;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.FileTypesDto;
import com.naskoni.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books")
@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private static final String FILE_NAME = "export.";

  private final BookService bookService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(
      summary = "Create new book"
  )
  public BookResponseDto create(
      @Validated
      @RequestBody
      @Parameter(description = "Book object")
      BookRequestDto bookDto) {

    log.info("Create book request: {}", bookDto);

    BookResponseDto savedBookDto = bookService.create(bookDto);

    log.info("Created book response: {}", savedBookDto);

    return savedBookDto;
  }

  @PutMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(
      summary = "Update existing book"
  )
  public BookResponseDto update(
      @PathVariable
      @Parameter(description = "The id of the book for update")
      Long id,

      @Validated
      @RequestBody
      @Parameter(description = "Book object")
      BookRequestDto bookDto) {

    log.info("Update book request: {}", bookDto);

    BookResponseDto savedBookDto = bookService.update(id, bookDto);

    log.info("Updated book response: {}", savedBookDto);

    return savedBookDto;
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Delete existing book",
      description = "Deletes a book only if it is not in use on lend."
  )
  public void delete(
      @PathVariable
      @Parameter(description = "The id of the book to delete")
      Long id) {

    bookService.delete(id);
  }

  @GetMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(
      summary = "Find book by id"
  )
  public BookResponseDto findOne(
      @PathVariable
      @Parameter(description = "The id of the book to retrieve")
      Long id) {

    return bookService.findOne(id);
  }

  @GetMapping
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(
      summary = "Find all books",
      description = "Retrieves a list of all books. Supports paging and sorting (optional)."
  )
  public Page<BookResponseDto> findAll(

      @Parameter(
          name = "search",
          description = "Search query by Book property, supported operations are >, <, :",
          example = "name:Quixote,author:Cervantes"
      )
      @RequestParam(value = "search", required = false)
      String search,

      @Parameter(hidden = true)
      Pageable pageable) {

    return bookService.findAll(search, pageable);
  }

  @GetMapping(value = "/files/{type}")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Export all book records as a file",
      description =
          "By default export as CSV file is supported. " +
              "Can be expanded with different implementations of the Exporter interface. " +
              "For example the provided XmlFileExporter.class can be loaded from " +
              "pluginClasses directory."
  )
  public ResponseEntity<byte[]> exportAsFile(
      @PathVariable("type")
      @Parameter(description = "The type of file to export")
      String fileType)
      throws IOException {

    byte[] file = bookService.export(fileType.toLowerCase());

    if (file != null) {
      return ResponseEntity.ok()
          .headers(buildHeaders(fileType))
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);
    }

    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping(value = "/files")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Retrieve supported file types"
  )
  public FileTypesDto retrieveSupportedFileTypes() {
    return bookService.retrieveSupportedFileTypes();
  }

  private HttpHeaders buildHeaders(String fileType) {
    HttpHeaders headers = new HttpHeaders();

    headers.set(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename = " + FILE_NAME + fileType
    );

    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");

    return headers;
  }
}