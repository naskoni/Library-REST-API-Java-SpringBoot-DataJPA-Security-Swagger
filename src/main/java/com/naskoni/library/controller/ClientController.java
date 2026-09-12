package com.naskoni.library.controller;

import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.service.ClientService;
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

@Tag(name = "Clients")
@Slf4j
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(summary = "Create new client")
  public ClientResponseDto create(
      @Validated
      @RequestBody
      @Parameter(description = "Client object")
      ClientRequestDto clientDto) {

    log.info("Create client request: {}", clientDto);

    ClientResponseDto savedClient = clientService.create(clientDto);

    log.info("Created client response: {}", savedClient);

    return savedClient;
  }

  @PutMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(summary = "Update existing client")
  public ClientResponseDto update(
      @PathVariable
      @Parameter(description = "The id of the client for update")
      Long id,

      @Validated
      @RequestBody
      @Parameter(description = "Client object")
      ClientRequestDto clientDto) {

    log.info("Update client request: {}", clientDto);

    ClientResponseDto savedClient = clientService.update(id, clientDto);

    log.info("Updated client response: {}", savedClient);

    return savedClient;
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Delete existing client",
      description = "Deletes a client only if it is not in use on lend."
  )
  public void delete(
      @PathVariable
      @Parameter(description = "The id of the client to delete")
      Long id) {

    clientService.delete(id);
  }

  @GetMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(summary = "Find client by id")
  public ClientResponseDto findOne(
      @PathVariable
      @Parameter(description = "The id of the client to retrieve")
      Long id) {

    return clientService.findOne(id);
  }

  @GetMapping
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @Operation(
      summary = "Find all clients",
      description = "Retrieves a list of all clients. Supports paging and sorting (optional)."
  )
  public Page<ClientResponseDto> findAll(

      @Parameter(
          name = "search",
          description = "Search query by Client property, supported operations are >, <, :",
          example = "name:George"
      )
      @RequestParam(value = "search", required = false)
      String search,

      @Parameter(hidden = true)
      Pageable pageable) {

    return clientService.findAll(search, pageable);
  }
}