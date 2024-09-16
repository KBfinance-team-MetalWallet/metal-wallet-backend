package com.kb.wallet.ticket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketDto {

  private long musicalId;
  private long seatId;
  private long scheduleId;

}
