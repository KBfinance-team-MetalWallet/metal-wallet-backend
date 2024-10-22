package com.kb.wallet.global.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.Base64;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAUtil {

  private static final String ALGORITHM = "RSA";
  private static final String PROVIDER = "BC";
  private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
    keyGen.initialize(2048); // 2048 비트 키 크기 사용
    return keyGen.generateKeyPair();
  }

  public static String encrypt(String message, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encryptedBytes = cipher.doFinal(message.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
    return new String(decryptedBytes);
  }

  // 공개키를 문자열로 변환
  public static String publicKeyToString(PublicKey publicKey) {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  public static String privateKeyToString(PrivateKey privateKey) {
    return Base64.getEncoder().encodeToString(privateKey.getEncoded());
  }

  // 문자열에서 공개키로 변환
  public static PublicKey stringToPublicKey(String publicKeyStr) throws Exception {
    byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(publicBytes));
  }

  public static PrivateKey stringToPrivateKey(String privateKeyStr) throws Exception {
    byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr); // Base64 디코딩
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM); // RSA 알고리즘을 사용하는 KeyFactory 생성
    return keyFactory.generatePrivate(
      new java.security.spec.PKCS8EncodedKeySpec(privateBytes)); // 개인키로 변환
  }

  public static String sign(String data, PrivateKey privateKey) throws Exception {
    Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
    signature.initSign(privateKey);
    signature.update(data.getBytes());
    return Base64.getEncoder().encodeToString(signature.sign());
  }

  public static boolean verify(String data, String signature, PublicKey publicKey)
    throws Exception {
    Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
    verifier.initVerify(publicKey);
    verifier.update(data.getBytes());
    return verifier.verify(Base64.getDecoder().decode(signature));
  }
}