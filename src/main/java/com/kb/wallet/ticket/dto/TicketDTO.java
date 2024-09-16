package com.kb.wallet.ticket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TicketDTO {

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class TicketRequest {
    private long musicalId;
    private long seatId;
    private long scheduleId;
  }

}
