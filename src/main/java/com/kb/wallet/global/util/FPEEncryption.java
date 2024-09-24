package com.kb.wallet.global.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
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
    FPEFF1Engine engine = new FPEFF1Engine();

    byte[] tweakBytes = tweak.getBytes(StandardCharsets.UTF_8);
    byte[] inputBytes = data.getBytes(StandardCharsets.UTF_8);

    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(true, params);

    byte[] outputBytes = new byte[inputBytes.length];
    engine.processBlock(inputBytes, 0, inputBytes.length, outputBytes, 0);

    // 데이터 처리 후 메모리에서 삭제
    Arrays.fill(inputBytes, (byte) 0);
    return Hex.toHexString(outputBytes);  // Hex로 출력 데이터 인코딩
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
}