package com.naskoni.library.controller;

import com.naskoni.library.dto.LendRequestDto;
import com.naskoni.library.dto.LendResponseDto;
import com.naskoni.library.service.LendService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "Lends")
@Slf4j
@RestController
@RequestMapping("/lends")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
@CrossOrigin(origins = "*")
public class LendController {

  @Autowired private LendService lendService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create new lend", response = LendResponseDto.class)
  public LendResponseDto create(
      @Validated @RequestBody @ApiParam(value = "Lend object") LendRequestDto lendRequestDto) {
    log.info("Create lend request: " + lendRequestDto.toString());
    LendResponseDto lendResponseDto = lendService.create(lendRequestDto);
    log.info("Created lend response: " + lendResponseDto.toString());
    return lendResponseDto;
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update existing lend", response = LendResponseDto.class)
  public LendResponseDto update(
      @PathVariable @ApiParam(value = "The id of the lend for update") Long id,
      @Validated @RequestBody @ApiParam(value = "Lend object") LendRequestDto lendRequestDto) {
    log.info("Update lend request: " + lendRequestDto.toString());
    LendResponseDto lendResponseDto = lendService.update(id, lendRequestDto);
    log.info("Updated lend response: " + lendResponseDto.toString());
    return lendResponseDto;
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Find lend by id")
  public LendResponseDto findOne(
      @PathVariable @ApiParam(value = "The id of the lend to retrieve") Long id) {
    return lendService.findOne(id);
  }

  @GetMapping
  @ApiOperation(
      value = "Find all lends",
      notes = "Retrieves a list of all lends. Supports paging and sorting (optional).",
      responseContainer = "List",
      response = LendResponseDto.class)
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
  public Page<LendResponseDto> findAll(
      @ApiParam(
              name = "search",
              value = "Search query by Lend property, supported operations are >, <, :",
              example = "lendingDate:2020-01-01")
          @RequestParam(value = "search", required = false)
          String search,
      @ApiIgnore Pageable pageable) {
    return lendService.findAll(search, pageable);
  }
}
