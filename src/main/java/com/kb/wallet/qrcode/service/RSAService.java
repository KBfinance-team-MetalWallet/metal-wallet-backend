package com.kb.wallet.qrcode.service;

import java.security.KeyPair;

public interface RSAService {

  KeyPair generateKeyPair() throws Exception; // 키 쌍 생성

  String encrypt(String plainText, String publicKey) throws Exception; // 암호화

  String decrypt(String encryptedText, String privateKey) throws Exception; // 복호화
}
