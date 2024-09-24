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

  private static final int RADIX = 10;  // 10진법 사용

  // QR 코드 데이터를 FPE로 복호화
  public String decrypt(String encryptedData, String tweak, SecretKey secretKey,
      String expectedHMAC) throws Exception {
    // 입력값 검증 : Hex 형식 검증
    if (!isHex(encryptedData)) {
      throw new IllegalArgumentException("암호화된 데이터는 유효한 Hex 형식이어야 합니다.");
    }
    // 입력값 검증: tweak 값은 UTF-8 형식어야 한다.
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }

    // FPEFF1Engine 생성
    FPEFF1Engine engine = new FPEFF1Engine();

    // Tweak 및 암호화된 데이터 바이트 배열로 변환
    byte[] tweakBytes = tweak.getBytes(StandardCharsets.UTF_8);  // Tweak 값을 바이트로 변환
    byte[] encryptedBytes = Hex.decode(encryptedData);  // 암호화된 데이터를 바이트 배열로 변환

    // FPE 파라미터 설정 (키, RADIX, Tweak)
    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(false, params);  // 복호화 모드로 엔진 초기화

    // 복호화 처리
    byte[] decryptedBytes = new byte[encryptedBytes.length];  // 복호화 결과를 담을 배열
    int bytesProcessed = engine.processBlock(encryptedBytes, 0, encryptedBytes.length,
        decryptedBytes, 0);// 복호화 수행

    // 복호화된 결과를 UTF-8 문자열로 반환
    String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);

    // 출력값 검증 : 복호화된 데이터가 예상된 형식인지 확인
    if (!isVaildQRCode(decryptedData)) {
      throw new IllegalArgumentException("복호화된 데이터가 예상된 형식이 아닙니다.");
    }

    // 복호화된 데이터의 무결성 검증 (HMAC)
    if (!verifyHMAC(decryptedData, secretKey, expectedHMAC)) {
      throw new SecurityException("데이터 무결성 검증에 실패했습니다. 데이터가 위조되었을 수 있습니다.");
    }
    return decryptedData;
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

  // QR 코드 데이터 형식 검증(예시로 URL 패턴 확인)
  private boolean isVaildQRCode(String data) {
    // 예를 들어, QR코드가 URL 형식이어야 한다면
    String urlPattern = "^(http|https)://.*$";
    return Pattern.matches(urlPattern, data);
  }

  // 문자열 키를 사용하여 데이터 복호화
  public String decryptWithStringKey(String encryptedData, String tweak, String key,
      String expectedHMAC)
      throws Exception {
    // 문자열로부터 SecretKey를 복원
    SecretKey secretKey = FPEEncryption.getKeyFromString(key);
    return decrypt(encryptedData, tweak, secretKey, expectedHMAC);
  }
}
