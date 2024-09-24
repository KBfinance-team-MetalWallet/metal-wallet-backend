package com.kb.wallet.global.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
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

    byte[] tweakBytes = tweak.getBytes();
    byte[] inputBytes = data.getBytes();

    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(true, params);

    byte[] outputBytes = new byte[inputBytes.length];
    engine.processBlock(inputBytes, 0, inputBytes.length, outputBytes, 0);

    return new String(outputBytes);
  }

  // 비밀키 생성
  private SecretKey generateKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128);  // AES 128비트 키
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