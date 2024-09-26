package com.kb.wallet.ticket.dto.response;

import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import java.time.LocalDateTime;
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
  private LocalDateTime createdAt;
  private LocalDateTime validUntil;
  private LocalDateTime cancelUntil;

  public static TicketResponse toTicketResponse(Ticket ticket) {
    return TicketResponse.builder()
        .id(ticket.getId())
        .ticketStatus(ticket.getTicketStatus())
        .createdAt(ticket.getCreatedAt())
        .validUntil(ticket.getValidUntil())
        .cancelUntil(ticket.getCancelUntil())
        .build();
  }

}
