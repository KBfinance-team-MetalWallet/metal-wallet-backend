package com.kb.wallet.global.util;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
public final class RSAUtil {

  private static final String ALGORITHM = "RSA";
  private static final String PROVIDER = "BC";
  private static final String OAEP_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

  static {
    try {
      if (Security.getProvider(PROVIDER) == null) {
        Security.addProvider(new BouncyCastleProvider());
      }
    } catch (Exception e) {
      log.error("Failed to add Bouncy Castle provider", e);
      throw new CustomException(ErrorCode.KEY_VALIDATION_ERROR);
    }
  }

  private RSAUtil() {
    throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
  }

  public static KeyPair generateKeyPair() {
    try {
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
      keyPairGen.initialize(2048);
      return keyPairGen.generateKeyPair();
    } catch (Exception e) {
      log.error("RSA 키 쌍 생성 실패", e);
      throw new CustomException(ErrorCode.KEY_GENERATION_ERROR);
    }
  }

  public static String decrypt(String base64EncryptedData, PrivateKey privateKey) {
    try {
      Cipher cipher = Cipher.getInstance(OAEP_PADDING, PROVIDER);
      OAEPParameterSpec oaepParams = new OAEPParameterSpec(
          "SHA-256",
          "MGF1",
          new MGF1ParameterSpec("SHA-256"),
          PSource.PSpecified.DEFAULT
      );
      cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64EncryptedData));
      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("RSA 복호화 실패", e);
      throw new CustomException(ErrorCode.RSA_DECRYPTION_ERROR);
    }
  }

  public static String encrypt(String data, PublicKey publicKey) {
    try {
      Cipher cipher = Cipher.getInstance(OAEP_PADDING, PROVIDER);
      OAEPParameterSpec oaepParams = new OAEPParameterSpec(
          "SHA-256",
          "MGF1",
          new MGF1ParameterSpec("SHA-256"),
          PSource.PSpecified.DEFAULT
      );
      cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      log.error("RSA 암호화 실패", e);
      throw new CustomException(ErrorCode.RSA_ENCRYPTION_ERROR);
    }
  }
}