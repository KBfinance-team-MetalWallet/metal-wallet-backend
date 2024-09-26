package com.kb.wallet.qrcode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncrypeDataDto {

  String encryptedData;
  String securityCode;
  byte[] iv;
}
