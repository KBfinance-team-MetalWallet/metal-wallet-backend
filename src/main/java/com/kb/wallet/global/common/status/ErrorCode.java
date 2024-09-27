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


    // Ticket Errors
    TICKET_NOT_FOUND_ERROR(404, "TI001", "티켓을 찾을 수 없습니다"),
    TICKET_STATUS_INVALID(400, "TI002", "티켓 상태가 유효하지 않습니다"),

    // Account Errors
    ACCOUNT_NOT_FOUND_ERROR(404, "AC001", "계좌를 찾을 수 없습니다"),

    /**
     * ******************************* Custom Error CodeList
     * ***************************************
     */
    // Transaction Insert Error
    INSERT_ERROR(200, "9999", "Transaction 삽입 오류 발생"),

    // Transaction Update Error
    UPDATE_ERROR(200, "9999", "Transaction 수정 오류 발생"),

    // Transaction Delete Error
    DELETE_ERROR(200, "9999", "Transaction 삭제 오류 발생"),

    ; // End

    /**
     * ******************************* Error Code Constructor
     * ***************************************
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