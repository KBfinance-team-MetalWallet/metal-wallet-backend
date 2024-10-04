package com.kb.wallet.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SignedTicketResponse {

  private TicketResponse ticketInfo;
  private String signature;
}

