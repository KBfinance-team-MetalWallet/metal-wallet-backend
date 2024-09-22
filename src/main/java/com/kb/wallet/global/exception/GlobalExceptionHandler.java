package com.kb.wallet.global.exception;

import com.fasterxml.jackson.core.*;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.common.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 공통된 응답 생성 메서드
  private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String message, HttpStatus status) {
    log.error("Error occurred: {}", message);
    ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode, message, status);
    return new ResponseEntity<>(ErrorResponse.of(errorResponseDto.getErrorCode(), errorResponseDto.getMessage()), status);
  }

  // 유효성 검사 실패 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    String errorMessage = getFieldErrorMessages(ex.getBindingResult());
    return buildErrorResponse(ErrorCode.NOT_VALID_ERROR, errorMessage, HttpStatus.BAD_REQUEST);
  }

  // 필드 오류 메시지 추출
  private String getFieldErrorMessages(BindingResult bindingResult) {
    StringBuilder errors = new StringBuilder();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errors.append(fieldError.getField())
          .append(": ")
          .append(fieldError.getDefaultMessage())
          .append(", ");
    }
    return errors.toString();
  }

  // 요청 헤더 누락 처리
  @ExceptionHandler(MissingRequestHeaderException.class)
  protected ResponseEntity<ErrorResponse> handleMissingRequestHeader(MissingRequestHeaderException ex) {
    return buildErrorResponse(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // 메시지 읽기 불가 (본문이 누락된 경우)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
    return buildErrorResponse(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // 요청 파라미터 누락 처리
  @ExceptionHandler(MissingServletRequestParameterException.class)
  protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
    return buildErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // 잘못된 요청 처리
  @ExceptionHandler(HttpClientErrorException.BadRequest.class)
  protected ResponseEntity<ErrorResponse> handleBadRequest(HttpClientErrorException.BadRequest ex) {
    return buildErrorResponse(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // 핸들러를 찾지 못한 경우
  @ExceptionHandler(NoHandlerFoundException.class)
  protected ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
    return buildErrorResponse(ErrorCode.NOT_FOUND_ERROR, ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // NullPointerException 처리
  @ExceptionHandler(NullPointerException.class)
  protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
    log.warn("Null pointer exception: {}", ex.getMessage());
    return buildErrorResponse(ErrorCode.NULL_POINT_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 입출력 오류 처리
  @ExceptionHandler(IOException.class)
  protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
    log.warn("IO exception: {}", ex.getMessage());
    return buildErrorResponse(ErrorCode.IO_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // JSON 파싱 오류 처리
  @ExceptionHandler(JsonParseException.class)
  protected ResponseEntity<ErrorResponse> handleJsonParseException(JsonParseException ex) {
    return buildErrorResponse(ErrorCode.JSON_PARSE_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // JSON 처리 오류 처리
  @ExceptionHandler(JsonProcessingException.class)
  protected ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
    return buildErrorResponse(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // 일반적인 모든 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error("Unhandled exception: {}", ex.getMessage(), ex);
    return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}