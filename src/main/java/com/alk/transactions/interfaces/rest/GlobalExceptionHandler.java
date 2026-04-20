package com.alk.transactions.interfaces.rest;

import com.alk.transactions.application.usecase.CreateTransactionUseCase.ParentTransactionNotFoundException;
import com.alk.transactions.application.usecase.CreateTransactionUseCase.TransactionAlreadyExistsException;
import com.alk.transactions.application.usecase.GetTransactionSumUseCase.TransactionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TransactionAlreadyExistsException.class)
  public ResponseEntity<ApiErrorResponse> handleTransactionAlreadyExists(
      TransactionAlreadyExistsException ex,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler({ParentTransactionNotFoundException.class, TransactionNotFoundException.class})
  public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(
      RuntimeException ex,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidBody(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request payload", request);
  }

  private ResponseEntity<ApiErrorResponse> buildErrorResponse(
      HttpStatus status,
      String message,
      HttpServletRequest request) {
    ApiErrorResponse response = new ApiErrorResponse(status.value(), message, request.getRequestURI());
    return ResponseEntity.status(status).body(response);
  }

  public record ApiErrorResponse(int status, String error, String path) {}
}
