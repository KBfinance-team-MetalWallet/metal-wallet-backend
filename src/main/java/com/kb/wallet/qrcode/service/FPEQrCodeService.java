package com.kb.wallet.qrcode.service;

import com.kb.wallet.qrcode.dto.EncrypeDataDto;

public interface FPEQrCodeService {

  /**
   * 주어진 데이터를 암호화합니다.
   *
   * @param data 암호화할 데이터
   * @return 암호화된 데이터가 포함된 DTO
   * @throws Exception 암호화 과정에서 발생할 수 있는 예외
   */
  EncrypeDataDto encrypt(String data) throws Exception;

  /**
   * 주어진 DTO를 사용해 데이터를 복호화합니다.
   *
   * @param dto 복호화할 데이터가 포함된 DTO
   * @return 복호화된 텍스트
   * @throws Exception 복호화 과정에서 발생할 수 있는 예외
   */
  String decrypt(EncrypeDataDto dto) throws Exception;

  /**
   * SecretKey를 문자열 형태로 반환합니다.
   *
   * @return SecretKey의 문자열 표현
   */
  String getSecretKeyAsString();

  /**
   * 주어진 SecretKey가 유효한지 검증합니다.
   *
   * @param secretKey 검증할 SecretKey
   */
  void validateSecretKey(String secretKey);

  /**
   * 주어진 IV(Initial Vector)가 유효한지 검증합니다.
   *
   * @param iv 검증할 IV (16 바이트 배열)
   */
  void validateIv(byte[] iv);
}