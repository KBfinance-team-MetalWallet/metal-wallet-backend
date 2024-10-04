package com.kb.wallet.ticket.dto.response;

import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
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
public class TicketResponse {

  private Long id;
  private TicketStatus ticketStatus;
  private String createdAt;
  private String validUntil;
  private String cancelUntil;
  private String deviceId;

  public static TicketResponse toTicketResponse(Ticket ticket) {
    return TicketResponse.builder()
      .id(ticket.getId())
      .ticketStatus(ticket.getTicketStatus())
      .createdAt(ticket.getCreatedAt().toString())
      .validUntil(ticket.getValidUntil().toString())
      .cancelUntil(ticket.getCancelUntil().toString())
      .deviceId(ticket.getDeviceId())
      .build();
  }

}
