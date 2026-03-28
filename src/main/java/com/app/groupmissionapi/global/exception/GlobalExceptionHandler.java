package com.app.groupmissionapi.global.exception;

import com.app.groupmissionapi.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {

    ErrorCode errorCode = e.getErrorCode();

    return ResponseEntity
      .status(errorCode.getStatus())
      .body(ApiResponse.fail(errorCode.getCode(), e.getMessage()));
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {

    ErrorCode errorCode = e.getErrorCode();

    return ResponseEntity
      .status(errorCode.getStatus())
      .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {

    String message = e.getBindingResult().getFieldErrors().isEmpty()
      ? ErrorCode.INVALID_INPUT_VALUE.getMessage()
      : e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();

    return ResponseEntity
      .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
      .body(ApiResponse.fail(ErrorCode.INVALID_INPUT_VALUE.getCode(), message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    return ResponseEntity
      .status(errorCode.getStatus())
      .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
  }

}
