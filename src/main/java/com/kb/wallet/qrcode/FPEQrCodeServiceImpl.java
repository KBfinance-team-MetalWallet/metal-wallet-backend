package com.kb.wallet.qrcode;

import static com.kb.wallet.global.common.status.ErrorCode.DECRYPTION_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.ENCRYPTION_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.INVALID_TYPE_VALUE;

import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.global.util.BouncyCastleUtil;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class FPEQrCodeServiceImpl implements FPEQrCodeService {

  public static final int MAX_IV_INT = 16;

  private final SecretKey secretKey;
  private final byte[] iv;

  public FPEQrCodeServiceImpl() throws Exception {
    this.secretKey = BouncyCastleUtil.generateKey();
    this.iv = BouncyCastleUtil.generateIv();
  }

  @Override
  public EncrypeDataDto encrypt(String data) {
    validateInputData(data);
    try {
      String encryptedData = BouncyCastleUtil.encrypt(data, secretKey, iv);
      String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

      return new EncrypeDataDto(encryptedData, secretKeyString, iv);

    } catch (Exception e) {
      throw new CustomException(ENCRYPTION_ERROR, "암호화 중 오류가 발생했습니다.");
    }
  }

  @Override
  public String decrypt(EncrypeDataDto dto) throws Exception {
    validateEncryptedData(dto.getEncryptedData());
    try {
      return BouncyCastleUtil.decrypt(dto.getEncryptedData(), secretKey, dto.getIv());
    } catch (Exception e) {
      throw new CustomException(DECRYPTION_ERROR, "복호화 중 오류가 발생했습니다.");
    }
  }

  @Override
  public String getSecretKeyAsString() {
    return Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

  @Override
  public void validateSecretKey(String secretKey) {
    if (secretKey == null || secretKey.isEmpty()) {
      throw new CustomException(INVALID_TYPE_VALUE, "유효하지 않은 Secret Key입니다.");
    }
  }

  @Override
  public void validateIv(byte[] iv) {
    if (iv == null || iv.length != MAX_IV_INT) {
      throw new CustomException(INVALID_TYPE_VALUE, "유효하지 않은 IV입니다. IV는 16바이트여야 합니다.");
    }
  }

  // 입력 데이터 유효성 검사
  private void validateInputData(String data) {
    if (data == null || data.isEmpty()) {
      throw new CustomException(INVALID_TYPE_VALUE, "암호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
  }

  // 암호화된 데이터 유효성 검사
  private void validateEncryptedData(String encryptedData) {
    if (encryptedData == null || encryptedData.isEmpty()) {
      throw new CustomException(INVALID_TYPE_VALUE, "복호화할 데이터는 null이거나 비어 있을 수 없습니다.");
    }
  }
}