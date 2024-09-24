package com.kb.wallet.global.util;

public class FPEQrCodeService {

  private final FPEEncryption fpeEncryption;
  private final FPEDecryption fpeDecryption;

  public FPEQrCodeService() throws Exception {
    // Initialize FPE encryption and decryption classes
    this.fpeEncryption = new FPEEncryption();
    this.fpeDecryption = new FPEDecryption();
  }

  // Encrypt QR code data
  public String encryptQrCodeData(String data, String tweak) throws Exception {
    return fpeEncryption.encrypt(data, tweak);
  }

  // Decrypt QR code data
  public String decryptQrCodeData(String encryptedData, String tweak, String key,
      String expectedHMAC) throws Exception {
    return fpeDecryption.decryptWithStringKey(encryptedData, tweak, key, expectedHMAC);
  }

  // Get the secret key as a string
  public String getSecretKeyAsString() {
    return fpeEncryption.getSecretKeyAsString();
  }
}
