package com.naskoni.library.controller;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.BookResponseDto;
import com.naskoni.library.dto.FileTypesDto;
import com.naskoni.library.service.BookService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@Api(tags = "Books")
@Slf4j
@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {

  private static final String FILE_NAME = "export.";

  @Autowired private BookService bookService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Create new book", response = BookRequestDto.class)
  public BookResponseDto create(
      @Validated @RequestBody @ApiParam(value = "Book object") BookRequestDto bookDto) {
    log.info("Create book request: " + bookDto.toString());
    BookResponseDto savedbookDto = bookService.create(bookDto);
    log.info("Created book response: " + savedbookDto.toString());
    return savedbookDto;
  }

  @PutMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Update existing book", response = BookRequestDto.class)
  public BookResponseDto update(
      @PathVariable @ApiParam(value = "The id of the book for update") Long id,
      @Validated @RequestBody @ApiParam(value = "Book object") BookRequestDto bookDto) {
    log.info("Update book request: " + bookDto.toString());
    BookResponseDto savedbookDto = bookService.update(id, bookDto);
    log.info("Updated book response: " + savedbookDto.toString());
    return savedbookDto;
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Secured("ROLE_ADMIN")
  @ApiOperation(
      value = "Delete existing book",
      notes = "Deletes a book only if it is not in use on lend.")
  public void delete(@PathVariable @ApiParam(value = "The id of the book to delete") Long id) {
    bookService.delete(id);
  }

  @GetMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Find book by id")
  public BookResponseDto findOne(
      @PathVariable @ApiParam(value = "The id of the book to retrieve") Long id) {
    return bookService.findOne(id);
  }

  @GetMapping
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(
      value = "Find all books",
      notes = "Retrieves a list of all books. Supports paging and sorting (optional).",
      responseContainer = "List",
      response = BookRequestDto.class)
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "page",
        dataType = "int",
        paramType = "query",
        value = "The number of the results page you want to retrieve (0..N)."),
    @ApiImplicitParam(
        name = "size",
        dataType = "int",
        paramType = "query",
        value = "Number of records per page."),
    @ApiImplicitParam(
        name = "sort",
        allowMultiple = true,
        dataType = "string",
        paramType = "query",
        value =
            "Sorting criteria in the format: property(,asc|desc). "
                + "Default sort order is ascending. "
                + "Multiple sort criteria are supported.")
  })
  public Page<BookResponseDto> findAll(
      @ApiParam(
              name = "search",
              value = "Search query by Book property, supported operations are >, <, :",
              example = "name:Quixote,author:Cervantes")
          @RequestParam(value = "search", required = false)
          String search,
      @ApiIgnore Pageable pageable) {
    return bookService.findAll(search, pageable);
  }

  @GetMapping(value = "/files/{type}")
  @Secured("ROLE_ADMIN")
  @ApiOperation(
      value = "Export all book records as a file",
      notes =
          "By default export as CSV file is supported. Can be expanded with different implementations of the Exporter interface. "
              + "For example the provided XmlFileExporter.class can be loaded from pluginClasses directory.")
  public ResponseEntity<byte[]> exportAsFile(
      @PathVariable("type") @ApiParam(value = "The type of file to export") String fileType)
      throws IOException {
    byte[] file = bookService.export(fileType.toLowerCase());
    if (file != null) {
      return ResponseEntity.ok()
          .headers(buildHeaders(fileType))
          .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
          .body(file);
    }

    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping(value = "/files")
  @Secured("ROLE_ADMIN")
  @ApiOperation(value = "Retrieve supported file types")
  public FileTypesDto retrieveSupportedFileTypes() {
    return bookService.retrieveSupportedFileTypes();
  }

  private HttpHeaders buildHeaders(String fileType) {
    HttpHeaders headers = new HttpHeaders();

    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = " + FILE_NAME + fileType);
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");

    return headers;
  }
}
