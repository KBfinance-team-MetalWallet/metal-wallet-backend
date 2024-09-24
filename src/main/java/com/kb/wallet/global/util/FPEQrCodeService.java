package com.kb.wallet.global.util;

public class FPEQrCodeService {

  private final FPEEncryption fpeEncryption;
  private final FPEDecryption fpeDecryption;

  public FPEQrCodeService() throws Exception {
    // FPE 암호화 및 복호화 클래스 초기화
    this.fpeEncryption = new FPEEncryption();
    this.fpeDecryption = new FPEDecryption();
  }

  // QR 코드 데이터를 암호화하는 메서드
  public String encryptQrCodeData(String data, String tweak) throws Exception {
    validateInputData(data, tweak);
    return fpeEncryption.encrypt(data, tweak);
  }

  // QR 코드 데이터를 복호화하는 메서드
  public String decryptQrCodeData(String encryptedData, String tweak, String key,
      String expectedHMAC) throws Exception {
    validateEncryptedData(encryptedData, tweak);
    return fpeDecryption.decryptWithStringKey(encryptedData, tweak, key, expectedHMAC);
  }

  // 비밀키를 문자열 형식으로 반환하는 메서드
  public String getSecretKeyAsString() {
    return fpeEncryption.getSecretKeyAsString();
  }

  // 입력 데이터 유효성 검사
  private void validateInputData(String data, String tweak) {
    if (data == null || data.isEmpty()) {
      throw new IllegalArgumentException("암호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }
  }

  // 암호화된 데이터 유효성 검사
  private void validateEncryptedData(String encryptedData, String tweak) {
    if (encryptedData == null || encryptedData.isEmpty()) {
      throw new IllegalArgumentException("복호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }
  }
}
