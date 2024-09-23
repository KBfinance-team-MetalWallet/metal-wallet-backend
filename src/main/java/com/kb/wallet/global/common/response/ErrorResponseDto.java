package com.kb.wallet.global.common.response;

import com.kb.wallet.global.common.status.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
  private ErrorCode errorCode;
  private String message;
  private HttpStatus status;
}