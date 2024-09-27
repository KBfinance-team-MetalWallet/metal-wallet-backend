package com.kb.wallet.qrcode.controller;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.qrcode.dto.EncrypeDataDto;
import com.kb.wallet.qrcode.service.FPEQrCodeService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/qrcode")
@RequiredArgsConstructor
@Slf4j
public class FPEQrCodeController {

  private final FPEQrCodeService fpeQrCodeService;

  @PostMapping("/encrypt")
  public ResponseEntity<HashMap<String, Object>> encrypt(@RequestBody EncrypeDataDto dto)
      throws Exception {
    String data = dto.getEncryptedData();
    log.info("Encrypting data: {}", data);

    EncrypeDataDto encryptedDataDto = fpeQrCodeService.encrypt(data);
    HashMap<String, Object> response = new HashMap<>();
    response.put("dto", encryptedDataDto);

    return ResponseEntity.ok(response);
  }


  @GetMapping("/key")
  public ResponseEntity<String> getSecretKey() {
    String secretKeyString = fpeQrCodeService.getSecretKeyAsString();
    return ResponseEntity.ok(secretKeyString);
  }

  @PostMapping("/decrypt")
  public ResponseEntity<HashMap<String, String>> decrypt(@RequestBody EncrypeDataDto dto) {
    log.info("Decrypting data with security code: {}", dto.getSecurityCode());

    String decryptedText;
    try {
      decryptedText = fpeQrCodeService.decrypt(dto);
    } catch (CustomException e) {
      log.error("CustomException 발생: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("예외 발생: {}", e.getMessage());
      throw new CustomException(ErrorCode.DECRYPTION_ERROR,
          "복호화 중 오류가 발생했습니다.");
    }

    HashMap<String, String> response = new HashMap<>();
    response.put("decryptedText", decryptedText);

    return ResponseEntity.ok(response);
  }

}