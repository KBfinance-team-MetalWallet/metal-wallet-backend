package com.kb.wallet.qrcode.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecryptionRequest {

  @NotBlank(message = "암호화된 텍스트는 비어있을 수 없습니다")
  @JsonProperty("encrypted_text")
  private String encryptedText;

  @NotBlank(message = "개인키는 비어있을 수 없습니다")
  @JsonProperty("private_key")
  private String privateKey;  // Base64 인코딩된 개인키

//  @NotBlank(message = "device정보는 비어있을 수 없습니다")
//  @JsonProperty("deviceId")
//  private String deviceId;
}