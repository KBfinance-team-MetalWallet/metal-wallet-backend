package com.kb.wallet.ticket.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.global.util.RSAUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RSAServiceImplTest {

  @InjectMocks
  private RSAServiceImpl rsaService;

  private KeyPair keyPair;

  @BeforeEach
  void setUp() {
    // Given
    rsaService.init();
    try {
      // Given
      Field keyPairField = RSAServiceImpl.class.getDeclaredField("keyPair");
      keyPairField.setAccessible(true);
      keyPair = (KeyPair) keyPairField.get(rsaService);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Failed to access keyPair field using reflection");
    }
  }

  @Test
  @DisplayName("RSA 키 쌍 생성 테스트")
  void shouldInitializeKeyPair() {
    // Then
    assertNotNull(keyPair, "Key pair should be initialized");
    assertNotNull(keyPair.getPublic(), "Public key should be initialized");
    assertNotNull(keyPair.getPrivate(), "Private key should be initialized");
  }

  @Test
  @DisplayName("RSA PublicKey 반환 테스트")
  void shouldReturnPublicKey() {
    // When
    PublicKey publicKey = rsaService.getPublicKey();

    // Then
    assertNotNull(publicKey, "Public key should not be null");
    assertEquals("RSA", publicKey.getAlgorithm(), "Algorithm should be RSA");
  }

  @Test
  @DisplayName("RSA PrivateKey 반환 테스트")
  void shouldReturnPrivateKey() {
    // When
    PrivateKey privateKey = rsaService.getPrivateKey();

    // Then
    assertNotNull(privateKey, "Private key should not be null");
    assertEquals("RSA", privateKey.getAlgorithm(), "Algorithm should be RSA");
  }

  @Test
  @DisplayName("데이터 복호화 성공 테스트")
  void shouldDecryptEncryptedDataSuccessfully() {
    // Given
    String originalText = "This is a test message";
    String encryptedData = RSAUtil.encrypt(originalText, rsaService.getPublicKey());

    // When & Then
    assertDoesNotThrow(() -> {
      String decryptedText = rsaService.decrypt(encryptedData);
      assertEquals(originalText, decryptedText, "Decrypted text should match original");
    });
  }

  @Test
  @DisplayName("복호화 실패 시 예외 발생 테스트")
  void shouldThrowExceptionWhenDecryptionFails() {
    // Given
    String invalidData = "InvalidEncryptedData";

    // When
    CustomException exception = assertThrows(CustomException.class, () -> rsaService.decrypt(invalidData));

    // Then
    assertEquals(ErrorCode.RSA_DECRYPTION_ERROR, exception.getErrorCode(), "Error code should be RSA_DECRYPTION_ERROR");
    assertEquals("복호화 중 오류가 발생했습니다", exception.getMessage(), "Exception message should match");
  }
}