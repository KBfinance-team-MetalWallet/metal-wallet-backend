package com.kb.wallet.global.util;

import javax.crypto.SecretKey;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class FPEDecryption {

  private static final int RADIX = 10;  // 10진법 사용

  // QR 코드 데이터를 FPE로 복호화
  public String decrypt(String encryptedData, String tweak, SecretKey secretKey) throws Exception {
    // FPEFF1Engine 생성
    FPEFF1Engine engine = new FPEFF1Engine();

    // Tweak 및 암호화된 데이터 바이트 배열로 변환
    byte[] tweakBytes = tweak.getBytes();  // Tweak 값을 바이트로 변환
    byte[] encryptedBytes = Hex.decode(encryptedData);  // 암호화된 데이터를 바이트 배열로 변환

    // FPE 파라미터 설정 (키, RADIX, Tweak)
    FPEParameters params = new FPEParameters(new KeyParameter(secretKey.getEncoded()), RADIX,
        tweakBytes);
    engine.init(false, params);  // 복호화 모드로 엔진 초기화

    // 복호화 처리
    byte[] decryptedBytes = new byte[encryptedBytes.length];  // 복호화 결과를 담을 배열
    int bytesProcessed = engine.processBlock(encryptedBytes, 0, encryptedBytes.length,
        decryptedBytes, 0);// 복호화 수행

    // 복호화된 결과를 문자열로 반환
    return new String(decryptedBytes);
  }

  // 문자열 키를 사용하여 데이터 복호화
  public String decryptWithStringKey(String encryptedData, String tweak, String key)
      throws Exception {
    // 문자열로부터 SecretKey를 복원
    SecretKey secretKey = FPEEncryption.getKeyFromString(key);
    return decrypt(encryptedData, tweak, secretKey);
  }
}
