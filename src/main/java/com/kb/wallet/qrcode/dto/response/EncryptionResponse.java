package com.kb.wallet.qrcode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionResponse {

  private String plaintext;
  private String publicKey;
  private String encryptedData;
//  @JsonProperty("validity_duration")
//  private int validityDuration;
}