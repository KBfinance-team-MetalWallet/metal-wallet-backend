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

  static {
    // BouncyCastle 등록
    Security.addProvider(new BouncyCastleProvider());
  }

  // AES 암호화 메서드 (보안 강화)
  public static String encrypt(String plainText, SecretKey secretKey, byte[] iv) throws Exception {
    // Cipher에 사용할 AES 알고리즘과 CBC 모드, PKCS5Padding 설정
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

    // IV(초기화 벡터)를 설정
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

    // 암호화 모드로 초기화 (Encryption)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

    // 데이터 암호화
    byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

    // Base64로 인코딩하여 반환
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  // AES 복호화 메서드 (보안 강화)
  public static String decrypt(String encryptedText, SecretKey secretKey, byte[] iv)
      throws Exception {
    // 복호화에 사용할 AES 알고리즘과 CBC 모드, PKCS5Padding 설정
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

    // IV(초기화 벡터)를 설정
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

    // 복호화 모드로 초기화 (Decryption)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

    // Base64로 디코딩 후 복호화
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
    byte[] decryptedBytes = cipher.doFinal(decodedBytes);

    // UTF-8 문자열로 반환
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  // SecretKey 생성 메서드 (AES-256, 보안 강화)
  public static SecretKey generateKey() throws Exception {
    // 키 생성에 사용할 SecureRandom을 BouncyCastle로 초기화
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");

    // 256비트 키 생성
    keyGenerator.init(256, new SecureRandom());

    return keyGenerator.generateKey();
  }

  // 16바이트 IV(초기화 벡터) 생성 (보안 강화)
  public static byte[] generateIv() {
    // SecureRandom을 사용하여 안전한 IV 생성
    byte[] iv = new byte[16]; // AES는 16바이트 IV 사용
    SecureRandom secureRandom = new SecureRandom(); // 보안 랜덤 생성기 사용
    secureRandom.nextBytes(iv);
    return iv;
  }

  // 보안 강화를 위한 SecretKey 및 IV 검증 메서드 추가
  public static void validateSecretKey(SecretKey secretKey) {
    if (secretKey == null || secretKey.getEncoded().length != 32) {
      throw new IllegalArgumentException("유효하지 않은 비밀키입니다. AES-256은 256비트 키가 필요합니다.");
    }
  }

  public static void validateIv(byte[] iv) {
    if (iv == null || iv.length != 16) {
      throw new IllegalArgumentException("유효하지 않은 IV입니다. AES는 16바이트 IV가 필요합니다.");
    }
  }
}