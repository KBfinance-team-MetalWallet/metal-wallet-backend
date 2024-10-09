package com.kb.wallet.ticket.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProposedEncryptResponse {

  private String publicKey;
  private TicketInfo ticketInfo;
  private Long seconds;
}
