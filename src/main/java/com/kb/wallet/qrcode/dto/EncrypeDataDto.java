package com.kb.wallet.qrcode.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EncrypeDataDto {

  private final String encryptedData;
  private final String securityCode;
  private final byte[] iv;

  @JsonCreator
  public EncrypeDataDto(
      @JsonProperty("encryptedData") String encryptedData,
      @JsonProperty("securityCode") String securityCode,
      @JsonProperty("iv") byte[] iv) {
    this.encryptedData = encryptedData;
    this.securityCode = securityCode;
    this.iv = iv;
  }
}