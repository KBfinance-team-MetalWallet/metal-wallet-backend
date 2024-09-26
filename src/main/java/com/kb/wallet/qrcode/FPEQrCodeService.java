package com.kb.wallet.qrcode;

public interface FPEQrCodeService {

  EncrypeDataDto encrypt(String data) throws Exception;

  String decrypt(EncrypeDataDto dto) throws Exception;

  String getSecretKeyAsString();

  // 추가된 유효성 검증 메서드
  void validateSecretKey(String secretKey);

  void validateIv(byte[] iv);
}
