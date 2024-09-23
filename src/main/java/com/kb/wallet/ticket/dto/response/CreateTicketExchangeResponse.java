package com.kb.wallet.ticket.dto.response;

import com.kb.wallet.ticket.domain.TicketExchange;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateTicketExchangeResponse {

  private Long id;
  private int preferredSeat;
  private int preferredSchedule;
  private LocalDateTime createdAt;

  public static CreateTicketExchangeResponse createTicketExchangeResponse(
      TicketExchange ticketExchange) {
    return CreateTicketExchangeResponse.builder()
        .id(ticketExchange.getId())
        .preferredSeat(ticketExchange.getPreferredSeat())
        .preferredSchedule(ticketExchange.getPreferredSchedule())
        .createdAt(ticketExchange.getCreatedAt())
        .build();
  }
}
