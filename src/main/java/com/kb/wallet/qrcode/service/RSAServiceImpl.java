package com.kb.wallet.qrcode.service;

import com.kb.wallet.global.util.RSAUtil;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RSAServiceImpl implements RSAService {

  @Override
  public KeyPair generateKeyPair() throws Exception {
    // RSAUtil의 generateKeyPair 메서드를 호출하여 키 쌍을 생성합니다.
    return RSAUtil.generateKeyPair();
  }

  @Override
  public String encrypt(String plainText, String publicKeyStr) throws Exception {
    // 문자열로 전달받은 publicKey를 PublicKey 객체로 변환
    PublicKey publicKey = RSAUtil.stringToPublicKey(publicKeyStr);
    // RSAUtil의 encrypt 메서드를 호출하여 암호화 수행
    return RSAUtil.encrypt(plainText, publicKey);
  }

  @Override
  public String decrypt(String encryptedText, String privateKeyStr) throws Exception {
    // 문자열로 전달받은 privateKey를 PrivateKey 객체로 변환
    PrivateKey privateKey = RSAUtil.stringToPrivateKey(privateKeyStr);
    // RSAUtil의 decrypt 메서드를 호출하여 복호화 수행
    return RSAUtil.decrypt(encryptedText, privateKey);
  }
}
