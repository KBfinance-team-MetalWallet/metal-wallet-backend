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
  private String preferredSchedule;
  private int preferredSeatIndex; // 좌석 최대 개수 300개
}
