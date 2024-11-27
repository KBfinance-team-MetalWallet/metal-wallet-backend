package com.kb.wallet.global.common.response;

import lombok.Builder;
import lombok.Getter;

/**
 * [공통] API Response 결과의 반환 값을 관리
 */
@Getter
public class ApiResponse<T> {

  // API 응답 결과 Response
  private final T result;

  // API 응답 코드 Response
  private final int resultCode;

  // API 응답 코드 Message
  private final String resultMsg;

  @Builder
  public ApiResponse(final T result, final int resultCode, final String resultMsg) {
    this.result = result;
    this.resultCode = resultCode;
    this.resultMsg = resultMsg;
  }

  public static ApiResponse<Void> ok() {
    return ok(null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(data, 200, "OK");
  }

  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(data, 201, "Created");
  }

  public static <T> ApiResponse<T> unauthorized(T data) {
    return new ApiResponse<>(data, 404, "Unauthorized");
  }
}
