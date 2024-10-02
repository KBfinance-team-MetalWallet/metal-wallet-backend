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
  private String encodedTicketInfo;
  private int second;
  private String privateKey;

  public static QrCreationResponse toQrCreationResponse(String token, String encodedTicketInfo,
      int second, String privateKey) {
    return QrCreationResponse.builder()
        .token(token)
        .encodedTicketInfo(encodedTicketInfo)
        .second(second)
        .privateKey(privateKey)
        .build();
  }
}