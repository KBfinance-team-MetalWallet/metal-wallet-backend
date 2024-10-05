package com.kb.wallet.ticket.dto.request;

import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerifyTicketRequest {

  private SignedTicketResponse ticket;
  private String signature;
  private String deviceId;

}
