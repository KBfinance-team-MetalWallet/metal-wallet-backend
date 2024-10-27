package com.kb.wallet.global.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RSAUtilTest {

  private static KeyPair keyPair;

  @BeforeAll
  public static void setup() {
    keyPair = RSAUtil.generateKeyPair();
  }

  @Test
  public void testGenerateKeyPair() {
    assertNotNull(keyPair, "키 쌍은 null이 아니어야 합니다");
    assertNotNull(keyPair.getPrivate(), "개인 키는 null이 아니어야 합니다");
    assertNotNull(keyPair.getPublic(), "공개 키는 null이 아니어야 합니다");
  }

  @Test
  public void testEncryptDecrypt() {
    String originalData = "안녕하세요, RSA!";
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();

    String encryptedData = RSAUtil.encrypt(originalData, publicKey);
    assertNotNull(encryptedData, "암호화된 데이터는 null이 아니어야 합니다");

    String decryptedData = RSAUtil.decrypt(encryptedData, privateKey);
    assertEquals(originalData, decryptedData, "복호화된 데이터는 원본 데이터와 일치해야 합니다");
  }

  @Test
  public void testDecryptWithWrongKey() {
    String originalData = "안녕하세요, RSA!";
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();

    String encryptedData = RSAUtil.encrypt(originalData, publicKey);

    // 잘못된 개인 키를 사용하여 복호화 시도
    KeyPair wrongKeyPair = RSAUtil.generateKeyPair();
    PrivateKey wrongPrivateKey = wrongKeyPair.getPrivate();

    CustomException exception = assertThrows(CustomException.class, () -> {
      RSAUtil.decrypt(encryptedData, wrongPrivateKey);
    });

    assertEquals(ErrorCode.RSA_DECRYPTION_ERROR, exception.getErrorCode(), "오류 코드는 일치해야 합니다");
  }
}