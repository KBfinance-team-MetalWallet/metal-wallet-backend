package com.kb.wallet.ticket.constant;

import lombok.Getter;

@Getter
public enum TicketStatus {
  BOOKED("BOOKED"),
  CANCELED("CANCELED"),
  EXCHANGE_REQUESTED("EXCHANGE_REQUESTED"),
  CHECKED("CHECKED");

  private final String status;

  TicketStatus(String status) {
    this.status = status;
  }

  public static TicketStatus convertToTicketStatus(String status) {
    if(status == null) {
      return null;
    }
    try {
      return TicketStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
