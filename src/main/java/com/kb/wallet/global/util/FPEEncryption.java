package com.kb.wallet.global.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class FPEEncryption {

  private static final int RADIX = 10;  // 10진법
  private SecretKey secretKey;

  public FPEEncryption() throws NoSuchAlgorithmException {
    this.secretKey = generateKey();
  }

  // QR 코드 데이터를 FPE로 암호화
  public String encrypt(String data, String tweak) throws Exception {
    // 입력값 검증: 데이터가 숫자인지 확인
    if (!isNumeric(data)) {
      throw new IllegalArgumentException("암호화할 데이터는 숫자 형식이어야 합니다.");
    }
    // 입력값 검증: tweak 값 확인
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }

    FPEFF1Engine engine = new FPEFF1Engine();

    byte[] tweakBytes = tweak.getBytes(StandardCharsets.UTF_8);
    byte[] inputBytes = data.getBytes(StandardCharsets.UTF_8);

    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(true, params); // 암호화 모드로 엔진 초기화

    byte[] outputBytes = new byte[inputBytes.length];
    engine.processBlock(inputBytes, 0, inputBytes.length, outputBytes, 0);// 암호화 수행

    // 데이터 처리 후 메모리에서 삭제
    Arrays.fill(inputBytes, (byte) 0);
    return Hex.toHexString(outputBytes);  // 결과를 Hex 문자열로 반환
  }

  // 비밀키 생성
  private SecretKey generateKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    SecureRandom secureRandom = new SecureRandom();
    keyGen.init(128, secureRandom);  // AES 128비트 키와 SecureRandom 사용
    return keyGen.generateKey();
  }

  // 비밀키를 문자열로 반환
  public String getSecretKeyAsString() {
    return Hex.toHexString(secretKey.getEncoded());
  }

  // 문자열 비밀키를 사용해 키 복원
  public static SecretKey getKeyFromString(String key) {
    byte[] decodedKey = Hex.decode(key);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
  }

  // 입력 데이터가 숫자인지 확인하는 메서드
  private boolean isNumeric(String data) {
    return data != null && data.matches("\\d+");  // 모든 문자가 숫자인지 확인
  }

  // HMAC을 사용하여 데이터 무결성 검증 (암호화 시 HMAC 계산)
  public String calculateHMAC(String data, SecretKey secretKey) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(secretKey);
    byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

    return Hex.toHexString(hmacBytes);
  }
}