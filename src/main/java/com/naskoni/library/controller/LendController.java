package com.naskoni.library.controller;

import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.service.LendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lends")
@Slf4j
@RestController
@RequestMapping("/lends")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
@RequiredArgsConstructor
public class LendController {

  private final LendService lendService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create new lend")
  public LendResponseDto create(
      @Validated
      @RequestBody
      @Parameter(description = "Lend object")
      LendRequestDto lendRequestDto) {

    log.info("Create lend request: {}", lendRequestDto);

    LendResponseDto lendResponseDto = lendService.create(lendRequestDto);

    log.info("Created lend response: {}", lendResponseDto);

    return lendResponseDto;
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing lend")
  public LendResponseDto update(
      @PathVariable
      @Parameter(description = "The id of the lend for update")
      Long id,

      @Validated
      @RequestBody
      @Parameter(description = "Lend object")
      LendRequestDto lendRequestDto) {

    log.info("Update lend request: {}", lendRequestDto);

    LendResponseDto lendResponseDto = lendService.update(id, lendRequestDto);

    log.info("Updated lend response: {}", lendResponseDto);

    return lendResponseDto;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find lend by id")
  public LendResponseDto findOne(
      @PathVariable
      @Parameter(description = "The id of the lend to retrieve")
      Long id) {

    return lendService.findOne(id);
  }

  @GetMapping
  @Operation(
      summary = "Find all lends",
      description = "Retrieves a list of all lends. Supports paging and sorting (optional)."
  )
  public Page<LendResponseDto> findAll(

      @Parameter(
          name = "search",
          description = "Search query by Lend property, supported operations are >, <, :",
          example = "lendingDate:2020-01-01"
      )
      @RequestParam(value = "search", required = false)
      String search,

      @Parameter(hidden = true)
      Pageable pageable) {

    return lendService.findAll(search, pageable);
  }
}