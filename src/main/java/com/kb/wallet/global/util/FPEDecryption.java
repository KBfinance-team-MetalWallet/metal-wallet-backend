package com.kb.wallet.global.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class FPEDecryption {

  private static final int RADIX = 10; // 10진법 사용

  // QR 코드 데이터를 FPE로 복호화
  public String decrypt(String encryptedData, String tweak, SecretKey secretKey,
      String expectedHMAC) throws Exception {
    validateInputs(encryptedData, tweak);

    FPEFF1Engine engine = new FPEFF1Engine();
    byte[] tweakBytes = tweak.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedBytes = Hex.decode(encryptedData);

    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(false, params); // 복호화 모드로 엔진 초기화

    byte[] decryptedBytes = new byte[encryptedBytes.length];
    int bytesProcessed = engine.processBlock(encryptedBytes, 0, encryptedBytes.length,
        decryptedBytes, 0); // 복호화 수행

    String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
    validateDecryptedData(decryptedData, secretKey, expectedHMAC);

    return decryptedData;
  }

  // 입력값 검증
  private void validateInputs(String encryptedData, String tweak) {
    if (!isHex(encryptedData)) {
      throw new IllegalArgumentException("암호화된 데이터는 유효한 Hex 형식이어야 합니다.");
    }
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }
  }

  // 복호화된 데이터 검증
  private void validateDecryptedData(String decryptedData, SecretKey secretKey, String expectedHMAC)
      throws Exception {
    if (!isValidQRCode(decryptedData)) {
      throw new IllegalArgumentException("복호화된 데이터가 예상된 형식이 아닙니다.");
    }
    if (!verifyHMAC(decryptedData, secretKey, expectedHMAC)) {
      throw new SecurityException("데이터 무결성 검증에 실패했습니다. 데이터가 위조되었을 수 있습니다.");
    }
  }

  // HMAC을 사용하여 데이터 무결성 검증
  private boolean verifyHMAC(String data, SecretKey secretKey, String expectedHMAC)
      throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(secretKey);
    byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

    String calculatedHMAC = Hex.toHexString(hmacBytes);
    return calculatedHMAC.equals(expectedHMAC);
  }

  // Hex 문자열 형식 검증
  private boolean isHex(String data) {
    return data != null && data.matches("[0-9a-fA-F]+$");
  }

  // QR 코드 데이터 형식 검증 (예시로 URL 패턴 확인)
  private boolean isValidQRCode(String data) {
    String urlPattern = "^(http|https)://.*$";
    return Pattern.matches(urlPattern, data);
  }

  // 문자열 키를 사용하여 데이터 복호화
  public String decryptWithStringKey(String encryptedData, String tweak, String key,
      String expectedHMAC) throws Exception {
    SecretKey secretKey = FPEEncryption.getKeyFromString(key);
    return decrypt(encryptedData, tweak, secretKey, expectedHMAC);
  }
}
