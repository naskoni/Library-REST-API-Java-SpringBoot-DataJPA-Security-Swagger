package com.naskoni.library.controller;

import com.naskoni.library.dto.BookRequestDto;
import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "Users")
@Slf4j
@RestController
@RequestMapping("/users")
@Secured("ROLE_ADMIN")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create new user", response = UserRequestDto.class)
  public UserResponseDto create(
      @Validated @RequestBody @ApiParam(value = "User object") UserRequestDto userRequestDto) {
    log.info("Create user request: " + userRequestDto.toString());
    UserResponseDto savedUserDto = userService.create(userRequestDto);
    log.info("Created user response: " + savedUserDto.toString());
    return savedUserDto;
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update existing user", response = UserRequestDto.class)
  public UserResponseDto update(
      @PathVariable @ApiParam(value = "The id of the user for update") Long id,
      @Validated @RequestBody @ApiParam(value = "User object") UserRequestDto userRequestDto) {
    log.info("Update user request: " + userRequestDto.toString());
    UserResponseDto savedUserDto = userService.update(id, userRequestDto);
    log.info("Updated user response: " + savedUserDto.toString());
    return savedUserDto;
  }

  @PatchMapping("/{id}")
  @ApiOperation(value = "Deactivate existing user", response = UserRequestDto.class)
  public UserResponseDto deactivate(
      @PathVariable @ApiParam(value = "The id of the user for deactivating") Long id) {
    UserResponseDto savedUserDto = userService.deactivate(id);
    log.info("Deactivated user response: " + savedUserDto.toString());
    return savedUserDto;
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Find user by id")
  public UserResponseDto findOne(
      @PathVariable @ApiParam(value = "The id of the user to retrieve") Long id) {
    return userService.findOne(id);
  }

  @GetMapping
  @ApiOperation(
      value = "Find all users",
      notes = "Retrieves a list of all users. Supports paging and sorting (optional).",
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
  public Page<UserResponseDto> findAll(
      @ApiParam(
              name = "search",
              value = "Search query by User property, supported operations are >, <, :",
              example = "username:admin")
          @RequestParam(value = "search", required = false)
          String search,
      @ApiIgnore Pageable pageable) {
    return userService.findAll(search, pageable);
  }
}
