package com.kb.wallet.qrcode.service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSAService {

  KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException;

  PublicKey getPublicKey();

  PrivateKey getPrivateKey();

  String encrypt(String plaintext, PublicKey publicKey) throws Exception;

  String decrypt(String encryptedData, PrivateKey privateKey) throws Exception;

  String sign(String data, PrivateKey privateKey) throws Exception;

  boolean verify(String data, String signature, PublicKey publicKey) throws Exception;

  String publicKeyToString(PublicKey publicKey);

  String privateKeyToString(PrivateKey privateKey);

  PublicKey stringToPublicKey(String publicKeyStr) throws Exception;

  PrivateKey stringToPrivateKey(String privateKeyStr) throws Exception;
}