package com.kb.wallet.qrcode;

import com.kb.wallet.global.util.BouncyCastleUtil;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class FPEQrCodeServiceImpl implements FPEQrCodeService {

  private final SecretKey secretKey;
  private final byte[] iv;

  public FPEQrCodeServiceImpl() throws Exception {
    this.secretKey = BouncyCastleUtil.generateKey();
    this.iv = BouncyCastleUtil.generateIv();
  }

  @Override
  public EncrypeDataDto encrypt(String data) throws Exception {
    String encryptedData = BouncyCastleUtil.encrypt(data, secretKey, iv);
    String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

    EncrypeDataDto dto = new EncrypeDataDto();
    dto.setEncryptedData(encryptedData);
    dto.setSecurityCode(secretKeyString);
    dto.setIv(iv);

    return dto;
  }

  @Override
  public String decrypt(EncrypeDataDto dto) throws Exception {
    return BouncyCastleUtil.decrypt(dto.getEncryptedData(), secretKey, dto.getIv());
  }

  @Override
  public String getSecretKeyAsString() {
    return Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

  // 유효성 검사 메서드 구현
  @Override
  public void validateSecretKey(String secretKey) {
    if (secretKey == null || secretKey.isEmpty()) {
      throw new IllegalArgumentException("Secret key는 null이거나 비어 있을 수 없습니다.");
    }
  }

  @Override
  public void validateIv(byte[] iv) {
    if (iv == null || iv.length != 16) {
      throw new IllegalArgumentException("IV는 null이거나 16바이트여야 합니다.");
    }
  }

  // 입력 데이터 유효성 검사
  private void validateInputData(String data) {
    if (data == null || data.isEmpty()) {
      throw new IllegalArgumentException("암호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
  }

  // 암호화된 데이터 유효성 검사
  private void validateEncryptedData(String encryptedData) {
    if (encryptedData == null || encryptedData.isEmpty()) {
      throw new IllegalArgumentException("복호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
  }
}
