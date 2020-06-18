package com.naskoni.library.controller;

import com.naskoni.library.dto.ClientRequestDto;
import com.naskoni.library.dto.ClientResponseDto;
import com.naskoni.library.service.ClientService;
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

@Api(tags = "Clients")
@Slf4j
@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "*")
public class ClientController {

  @Autowired private ClientService clientService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Create new client", response = ClientRequestDto.class)
  public ClientResponseDto create(
      @Validated @RequestBody @ApiParam(value = "Client object") ClientRequestDto clientDto) {
    log.info("Create client request: " + clientDto.toString());
    ClientResponseDto savedClient = clientService.create(clientDto);
    log.info("Created client response: " + savedClient.toString());
    return savedClient;
  }

  @PutMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Update existing client", response = ClientRequestDto.class)
  public ClientResponseDto update(
      @PathVariable @ApiParam(value = "The id of the client for update") Long id,
      @Validated @RequestBody @ApiParam(value = "Client object") ClientRequestDto clientDto) {
    log.info("Update client request: " + clientDto.toString());
    ClientResponseDto savedClient = clientService.update(id, clientDto);
    log.info("Updated client response: " + savedClient.toString());
    return savedClient;
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Secured({"ROLE_ADMIN"})
  @ApiOperation(
      value = "Delete existing client",
      notes = "Deletes a client only if it is not in use on lend.")
  public void delete(@PathVariable @ApiParam(value = "The id of the client to delete") Long id) {
    clientService.delete(id);
  }

  @GetMapping("/{id}")
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(value = "Find client by id")
  public ClientResponseDto findOne(
      @PathVariable @ApiParam(value = "The id of the client to retrieve") Long id) {
    return clientService.findOne(id);
  }

  @GetMapping
  @Secured({"ROLE_USER", "ROLE_ADMIN"})
  @ApiOperation(
      value = "Find all clients",
      notes = "Retrieves a list of all clients. Supports paging and sorting (optional).",
      responseContainer = "List",
      response = ClientRequestDto.class)
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
  public Page<ClientResponseDto> findAll(
      @ApiParam(
              name = "search",
              value = "Search query by Client property, supported operations are >, <, :",
              example = "name:George")
          @RequestParam(value = "search", required = false)
          String search,
      @ApiIgnore Pageable pageable) {
    return clientService.findAll(search, pageable);
  }
}
