package com.kb.wallet.global.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BouncyCastleUtil {

  private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final String PROVIDER = "BC";
  private static final int KEY_SIZE = 256;
  private static final int IV_SIZE = 16;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static String encrypt(String plainText, SecretKey secretKey, byte[] iv) throws Exception {
    validateSecretKey(secretKey);
    validateIv(iv);
    Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
    byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public static String decrypt(String encryptedText, SecretKey secretKey, byte[] iv)
      throws Exception {
    validateSecretKey(secretKey);
    validateIv(iv);
    Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
    byte[] decryptedBytes = cipher.doFinal(decodedBytes);
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  public static SecretKey generateKey() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", PROVIDER);
    keyGenerator.init(KEY_SIZE, new SecureRandom());
    return keyGenerator.generateKey();
  }

  public static byte[] generateIv() {
    byte[] iv = new byte[IV_SIZE];
    new SecureRandom().nextBytes(iv); // 보안 랜덤 생성기 사용
    return iv;
  }

  public static void validateSecretKey(SecretKey secretKey) {
    if (secretKey == null || secretKey.getEncoded().length != (KEY_SIZE / 8)) {
      throw new IllegalArgumentException("유효하지 않은 비밀키입니다. AES-256은 256비트 키가 필요합니다.");
    }
  }

  public static void validateIv(byte[] iv) {
    if (iv == null || iv.length != IV_SIZE) {
      throw new IllegalArgumentException("유효하지 않은 IV입니다. AES는 16바이트 IV가 필요합니다.");
    }
  }

  private static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) throws Exception {
    Cipher cipher = Cipher.getInstance(AES_ALGORITHM, PROVIDER);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    cipher.init(mode, secretKey, ivParameterSpec);
    return cipher;
  }
}