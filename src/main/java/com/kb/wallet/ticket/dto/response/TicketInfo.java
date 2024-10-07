package com.kb.wallet.ticket.dto.response;

import com.kb.wallet.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfo {

  private String cancelUntil;
  private String createdAt;
  private String TicketStatus;
  private String validUntil;
  private Long seatId;
  private Long memberId;

  public static TicketInfo fromTicket(Ticket ticket) {
    return TicketInfo.builder()
      .cancelUntil(ticket.getCancelUntil().toString())
      .createdAt(ticket.getCreatedAt().toString())
      .TicketStatus(ticket.getTicketStatus().toString())
      .validUntil(ticket.getValidUntil().toString())
      .seatId(ticket.getSeat().getId())
      .memberId(ticket.getMember().getId())
      .build();

  }

}
