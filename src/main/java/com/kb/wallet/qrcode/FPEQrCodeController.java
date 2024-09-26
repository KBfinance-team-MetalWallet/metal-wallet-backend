package com.kb.wallet.qrcode;

import java.util.HashMap;
import java.util.Map;
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
  public ResponseEntity<HashMap<String, Object>> encrypt(@RequestBody Map<String, String> paramMap)
      throws Exception {
    String data = paramMap.get("data");

    log.info("Encrypting data: {}", data);

    // 서비스에서 암호화 처리
    EncrypeDataDto encryptedDataDto = fpeQrCodeService.encrypt(data);
    HashMap<String, Object> response = new HashMap<>();
    response.put("dto", encryptedDataDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/key")
  public ResponseEntity<String> getSecretKey() {
    // 서비스에서 SecretKey 처리
    String secretKeyString = fpeQrCodeService.getSecretKeyAsString();
    return ResponseEntity.ok(secretKeyString);
  }

  @PostMapping("/decrypt")
  public ResponseEntity<HashMap<String, String>> decrypt(@RequestBody EncrypeDataDto dto)
      throws Exception {
    log.info("Decrypting data with security code: {}", dto.getSecurityCode());

    // 서비스에서 복호화 처리
    String decryptedText = fpeQrCodeService.decrypt(dto);
    HashMap<String, String> response = new HashMap<>();
    response.put("decryptedText", decryptedText);

    return ResponseEntity.ok(response);
  }
}