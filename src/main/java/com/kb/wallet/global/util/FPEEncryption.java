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

  private static final int RADIX = 10; // 10진법
  private final SecretKey secretKey;

  public FPEEncryption() throws NoSuchAlgorithmException {
    this.secretKey = generateKey();
  }

  public String encrypt(String data, String tweak) throws Exception {
    validateInputs(data, tweak);
    return performEncryption(data, tweak);
  }

  private void validateInputs(String data, String tweak) {
    if (!isNumeric(data)) {
      throw new IllegalArgumentException("암호화할 데이터는 숫자 형식이어야 합니다.");
    }
    if (tweak == null || tweak.isEmpty()) {
      throw new IllegalArgumentException("Tweak 값은 null이거나 비어 있을 수 없습니다.");
    }
  }

  private String performEncryption(String data, String tweak) throws Exception {
    FPEFF1Engine engine = new FPEFF1Engine();
    byte[] tweakBytes = tweak.getBytes(StandardCharsets.UTF_8);
    byte[] inputBytes = data.getBytes(StandardCharsets.UTF_8);

    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(true, params); // 암호화 모드로 엔진 초기화

    byte[] outputBytes = new byte[inputBytes.length];
    engine.processBlock(inputBytes, 0, inputBytes.length, outputBytes, 0); // 암호화 수행

    // 입력 바이트 배열 메모리에서 삭제
    Arrays.fill(inputBytes, (byte) 0);
    return Hex.toHexString(outputBytes); // 결과를 Hex 문자열로 반환
  }

  private SecretKey generateKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128, new SecureRandom()); // AES 128비트 키 생성
    return keyGen.generateKey();
  }

  public String getSecretKeyAsString() {
    return Hex.toHexString(secretKey.getEncoded());
  }

  public static SecretKey getKeyFromString(String key) {
    byte[] decodedKey = Hex.decode(key);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
  }

  private boolean isNumeric(String data) {
    return data != null && data.matches("\\d+"); // 모든 문자가 숫자인지 확인
  }

  public String calculateHMAC(String data, SecretKey secretKey) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(secretKey);
    byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return Hex.toHexString(hmacBytes);
  }
}
