package com.kb.wallet.global.exception;

import com.kb.wallet.global.common.status.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException implements CustomError {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public CustomException(ErrorCode errorCode, String customMessage) {
    super(customMessage);
    this.errorCode = errorCode;
  }

  @Override
  public ErrorCode getErrorCode() {
    return errorCode;
  }
}