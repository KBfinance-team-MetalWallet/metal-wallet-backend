package com.kb.wallet.global.common.status;

import lombok.Getter;

@Getter
public enum ErrorCode {
  /**
   * ******************************* Global Error CodeList ***************************************
   * HTTP Status Code 400 : Bad Request 401 : Unauthorized 403 : Forbidden 404 : Not Found 500 :
   * Internal Server Error
   * *********************************************************************************************
   */
  // 잘못된 서버 요청
  BAD_REQUEST_ERROR(400, "G001", "잘못된 요청"),

  // @RequestBody 데이터 미 존재
  REQUEST_BODY_MISSING_ERROR(400, "G002", "요청 본문이 존재하지 않습니다"),

  // 유효하지 않은 타입
  INVALID_TYPE_VALUE(400, "G003", "유효하지 않은 타입 값"),

  // Request Parameter 로 데이터가 전달되지 않을 경우
  MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "요청 파라미터가 누락되었습니다"),

  // 입력/출력 값이 유효하지 않음
  IO_ERROR(400, "G005", "입출력 오류"),

  // com.kb.wallet JSON 파싱 실패
  JSON_PARSE_ERROR(400, "G006", "JSON 파싱 실패"),

  // com.fasterxml.jackson.core Processing Error
  JACKSON_PROCESS_ERROR(400, "G007", "Jackson 처리 중 오류 발생"),

  // 권한이 없음
  FORBIDDEN_ERROR(403, "G008", "권한이 없습니다"),

  // 서버로 요청한 리소스가 존재하지 않음
  NOT_FOUND_ERROR(404, "G009", "요청한 리소스를 찾을 수 없습니다"),

  // NULL Point Exception 발생
  NULL_POINT_ERROR(404, "G010", "Null 포인터 예외 발생"),

  // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
  NOT_VALID_ERROR(404, "G011", "유효하지 않은 값"),

  // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
  NOT_VALID_HEADER_ERROR(404, "G012", "헤더에 데이터가 존재하지 않습니다"),

  // 서버가 처리 할 방법을 모르는 경우 발생
  INTERNAL_SERVER_ERROR(500, "G999", "서버 내부 오류"),

  /**
   * ******************************* Custom Error CodeList ***************************************
   */
  // Musical Errors
  MUSICAL_NOT_FOUND(404, "MU003", "요청한 뮤지컬을 찾을 수 없습니다."),
  // Member Errors
  MEMBER_NOT_FOUND_ERROR(404, "ME001", "사용자를 찾을 수 없습니다."),
  MEMBER_STATUS_INVALID(400, "ME002", "사용자의 ID가 유효하지 않습니다"),
  MEMBER_EMAIL_NOT_FOUND(404, "ME003", "해당 사용자의 이메일을 찾을 수 없습니다."),
  PIN_NUMBER_NOT_MATCH(404, "ME004", "일치하지 않는 핀 번호입니다."),
  // Ticket Errors
  TICKET_NOT_FOUND_ERROR(404, "TI001", "티켓을 찾을 수 없습니다."),
  TICKET_STATUS_INVALID(400, "TI002", "티켓 상태가 유효하지 않습니다."),
  TICKET_EXCHANGE_NOT_FOUND_ERROR(404, "TI003", "교환하기 위한 티켓을 찾을 수 없습니다."),

  // QR 코드 관련 에러 코드
  ENCRYPTION_ERROR(500, "EN001", "QR 코드 암호화 중 오류가 발생했습니다."),
  DECRYPTION_ERROR(500, "QR002", "QR 코드 복호화 중 오류가 발생했습니다."),
  QR_CODE_INVALID(400, "QR003", "유효하지 않은 QR 코드입니다."),
  QR_CODE_NOT_FOUND(404, "QR004", "QR 코드가 존재하지 않습니다."),
  INVALID_SECRET_KEY(500, "QR005", "유효하지 않은 비밀키입니다."),
  INVALID_IV(500, "QR006", "유효하지 않은 IV입니다."),

  // RSA 알고리즘 관련 에러 코드
  INVALID_KEY(400, "RSA001", "유효하지 않은 키입니다"),
  INVALID_KEY_SIZE(500, "RSA002", "유효하지 않은 키 크기입니다"),
  INVALID_KEY_TYPE(500, "RSA003", "RSA 키가 아닙니다"),
  KEY_GENERATION_ERROR(500, "RSA004", "키 생성 중 오류가 발생했습니다"),
  KEY_VALIDATION_ERROR(500, "RSA005", "키 검증 중 오류가 발생했습니다"),
  KEY_CONVERSION_ERROR(500, "RSA006", "키 변환 중 오류가 발생했습니다"),
  QR_CODE_GENERATION_ERROR(500, "RSA007", "QR Code 생성 중 오류가 발생했습니다."),
  DEVICE_ID_MISMATCH(400, "RSA008", "Device ID가 일치하지 않습니다."),
  // Account Errors
  ACCOUNT_NOT_FOUND_ERROR(404, "AC001", "계좌를 찾을 수 없습니다"),

  ACCOUNT_NOT_MATCH(404, "AC002", "계좌 명의가 불일치합니다."),

  // Transaction Insert Error
  INSERT_ERROR(200, "9999", "Transaction 삽입 오류 발생"),
  // Transaction Update Error
  UPDATE_ERROR(200, "9999", "Transaction 수정 오류 발생"),
  // Transaction Delete Error

  DELETE_ERROR(200, "9999", "Transaction 삭제 오류 발생"),

  // Seats Errors
  SEAT_NOT_FOUND_ERROR(404, "SE001", "좌석을 찾을 수 없습니다."),
  SEAT_ALREADY_BOOKED_ERROR(409, "SE002", "이미 예약된 좌석입니다."),
  NOT_ENOUGH_AVAILABLE_SEATS_ERROR(400, "SE003", "구역에 사용 가능한 좌석이 충분하지 않습니다.");


  /**
   * ******************************* Error Code Constructor ***************************************
   */
  // 에러 코드의 '코드 상태'를 반환한다.
  private final int status;

  // 에러 코드의 '코드간 구분 값'을 반환한다.
  private final String divisionCode;

  // 에러 코드의 '코드 메시지'를 반환한다.
  private final String message;

  // 생성자 구성
  ErrorCode(final int status, final String divisionCode, final String message) {
    this.status = status;
    this.divisionCode = divisionCode;
    this.message = message;
  }
}