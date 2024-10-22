package com.kb.wallet.ticket.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.global.util.RSAUtil;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class RSAServiceImpl implements RSAService {

  private KeyPair keyPair;

  @PostConstruct
  public void init() {
    try {
      if (this.keyPair == null) {
        this.keyPair = RSAUtil.generateKeyPair();
      }
    } catch (Exception e) {
      log.error("Failed to initialize RSA service", e);
      throw new CustomException(ErrorCode.KEY_GENERATION_ERROR, "RSA 키 쌍 생성 중 오류 발생");
    }
  }

  @Override
  public PublicKey getPublicKey() {
    return keyPair.getPublic();
  }

  @Override
  public PrivateKey getPrivateKey() {
    return keyPair.getPrivate();
  }

  @Override
  public String decrypt(String base64EncryptedData) {
    try {
      return RSAUtil.decrypt(base64EncryptedData, getPrivateKey());
    } catch (Exception e) {
      log.error("Failed to decrypt data", e);
      throw new CustomException(ErrorCode.RSA_DECRYPTION_ERROR, "복호화 중 오류가 발생했습니다");
    }
  }
}