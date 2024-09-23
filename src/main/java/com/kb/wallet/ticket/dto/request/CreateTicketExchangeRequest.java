package com.kb.wallet.ticket.dto.request;

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
public class CreateTicketExchangeRequest {
  private Long ticketId;
  private String preferredDate;
  private int preferredSchedule;
  private int preferredSeat;
}
