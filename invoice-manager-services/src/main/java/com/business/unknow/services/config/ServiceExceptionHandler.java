package com.business.unknow.services.config;

import com.business.unknow.model.error.ErrorMessage;
import com.business.unknow.model.error.InvoiceManagerException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {SQLException.class})
  public ResponseEntity<Object> handleSQLException(Exception ex, WebRequest request) {
    logger.error("SQLException ocurred", ex);
    return handleExceptionInternal(
        ex,
        new ErrorMessage(
            ex.getMessage(),
            "SQLException: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()),
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  @ExceptionHandler(value = {InvoiceManagerException.class})
  protected ResponseEntity<Object> handleServiceException(
      InvoiceManagerException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        ex.getErrorMessage(),
        new HttpHeaders(),
        HttpStatus.valueOf(ex.getErrorMessage().getHttpStatus()),
        request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    logger.warn("MethodArgumentNotValidException ocurred", ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {ResponseStatusException.class})
  public ResponseEntity<Object> handleResponseStatusException(
      ResponseStatusException ex, WebRequest request) {
    logger.warn(ex.getMessage());
    return handleExceptionInternal(
        ex,
        new ErrorMessage(ex.getReason(), ex.getMessage(), ex.getStatus().value()),
        new HttpHeaders(),
        ex.getStatus(),
        request);
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
    logger.error("Exception ocurred", ex);
    return handleExceptionInternal(
        ex,
        new ErrorMessage(
            "Unknown error has occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()),
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }
}
