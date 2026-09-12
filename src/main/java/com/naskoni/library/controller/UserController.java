package com.naskoni.library.controller;

import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.service.UserService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@Slf4j
@RestController
@RequestMapping("/users")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create new user")
  public UserResponseDto create(
      @Validated
      @RequestBody
      @Parameter(description = "User object")
      UserRequestDto userRequestDto) {

    log.info("Create user request: {}", userRequestDto);

    UserResponseDto savedUserDto = userService.create(userRequestDto);

    log.info("Created user response: {}", savedUserDto);

    return savedUserDto;
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing user")
  public UserResponseDto update(
      @PathVariable
      @Parameter(description = "The id of the user for update")
      Long id,

      @Validated
      @RequestBody
      @Parameter(description = "User object")
      UserRequestDto userRequestDto) {

    log.info("Update user request: {}", userRequestDto);

    UserResponseDto savedUserDto = userService.update(id, userRequestDto);

    log.info("Updated user response: {}", savedUserDto);

    return savedUserDto;
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Deactivate existing user")
  public UserResponseDto deactivate(
      @PathVariable
      @Parameter(description = "The id of the user for deactivating")
      Long id) {

    UserResponseDto savedUserDto = userService.deactivate(id);

    log.info("Deactivated user response: {}", savedUserDto);

    return savedUserDto;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find user by id")
  public UserResponseDto findOne(
      @PathVariable
      @Parameter(description = "The id of the user to retrieve")
      Long id) {

    return userService.findOne(id);
  }

  @GetMapping
  @Operation(
      summary = "Find all users",
      description = "Retrieves a list of all users. Supports paging and sorting (optional)."
  )
  public Page<UserResponseDto> findAll(

      @Parameter(
          name = "search",
          description = "Search query by User property, supported operations are >, <, :",
          example = "username:admin"
      )
      @RequestParam(value = "search", required = false)
      String search,

      @Parameter(hidden = true)
      Pageable pageable) {

    return userService.findAll(search, pageable);
  }
}