package com.kb.wallet.ticket.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketQrInfo {
  private Long memberId;
  private Long ticketId;
}