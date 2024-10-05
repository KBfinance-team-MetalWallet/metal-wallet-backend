package com.kb.wallet.qrcode.service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.springframework.stereotype.Service;

@Service
public class RSAServiceImpl implements RSAService {

  private static final String ALGORITHM = "RSA";
  private static final String PROVIDER = "BC";
  private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
  private final KeyPair keyPair;

  static {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
  }

  public RSAServiceImpl() throws NoSuchAlgorithmException, NoSuchProviderException {
    this.keyPair = generateKeyPair();
  }

  @Override
  public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
    keyGen.initialize(2048);
    return keyGen.generateKeyPair();
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
  public String encrypt(String plaintext, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  @Override
  public String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
    return new String(decryptedBytes);
  }

  @Override
  public String sign(String data, PrivateKey privateKey) throws Exception {
    Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
    signature.initSign(privateKey);
    signature.update(data.getBytes());
    return Base64.getEncoder().encodeToString(signature.sign());
  }

  @Override
  public boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
    Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
    verifier.initVerify(publicKey);
    verifier.update(data.getBytes());
    return verifier.verify(Base64.getDecoder().decode(signature));
  }

  @Override
  public String publicKeyToString(PublicKey publicKey) {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  @Override
  public String privateKeyToString(PrivateKey privateKey) {
    return Base64.getEncoder().encodeToString(privateKey.getEncoded());
  }

  @Override
  public PublicKey stringToPublicKey(String publicKeyStr) throws Exception {
    byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return keyFactory.generatePublic(keySpec);
  }

  @Override
  public PrivateKey stringToPrivateKey(String privateKeyStr) throws Exception {
    byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return keyFactory.generatePrivate(keySpec);
  }
}