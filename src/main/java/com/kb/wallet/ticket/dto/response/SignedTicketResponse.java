package com.kb.wallet.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SignedTicketResponse {

  private String encryptedTicketInfo;
  private String signature;
}

