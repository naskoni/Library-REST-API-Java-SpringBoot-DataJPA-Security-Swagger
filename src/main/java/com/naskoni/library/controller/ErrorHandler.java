package com.naskoni.library.controller;

import com.naskoni.library.dto.ApiErrorDto;
import com.naskoni.library.dto.ValidationErrorDTO;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.DuplicateException;
import com.naskoni.library.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiErrorDto handleAccessDeniedException(AccessDeniedException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.FORBIDDEN, e);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorDto handleNotFoundException(NotFoundException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler({DuplicateException.class, CurrentlyInUseException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiErrorDto handleConflictException(RuntimeException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorDTO handleConstraintViolationException(ConstraintViolationException ex) {
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    ValidationErrorDTO dto = new ValidationErrorDTO();

    violations.stream()
        .forEach(v -> dto.addFieldError(v.getPropertyPath().toString(), v.getMessage()));

    return dto;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> processDataIntegrityViolationException(
      DataIntegrityViolationException e, WebRequest request) {

    log.error(e.getMessage(), e);
    return handleExceptionInternal(
        e,
        createApiError(HttpStatus.BAD_REQUEST, e),
        new HttpHeaders(),
        HttpStatus.BAD_REQUEST,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    log.error(e.getMessage(), e);
    return handleExceptionInternal(
        e, createApiError(HttpStatus.BAD_REQUEST, e), headers, HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    log.error(e.getMessage(), e);
    return handleExceptionInternal(
        e, createApiError(HttpStatus.BAD_REQUEST, e), headers, HttpStatus.BAD_REQUEST, request);
  }

  private ApiErrorDto createApiError(HttpStatus httpStatus, Exception e) {
    ApiErrorDto dto = new ApiErrorDto();
    dto.setStatus(httpStatus.value());
    dto.setMessage(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
    dto.setRootCauseMessage(ExceptionUtils.getRootCauseMessage(e));

    return dto;
  }
}
