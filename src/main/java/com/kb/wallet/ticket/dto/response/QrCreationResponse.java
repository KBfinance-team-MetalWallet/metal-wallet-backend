package com.kb.wallet.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QrCreationResponse {
  private String token;
  private byte[] qrBytes;
  private int second;

  public static QrCreationResponse toQrCreationResponse(String token, byte[] qrBytes, int second) {
    return QrCreationResponse.builder()
        .token(token)
        .qrBytes(qrBytes)
        .second(second)
        .build();
  }
}