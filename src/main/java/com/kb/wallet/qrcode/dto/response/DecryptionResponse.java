package com.kb.wallet.qrcode.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionResponse {

  @JsonProperty("decrypted_text")
  private String decryptedText;
//  private String deviceId;
}