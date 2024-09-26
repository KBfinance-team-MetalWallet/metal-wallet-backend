package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;

public interface TicketCheckService {
  void checkTicketOwner(Ticket ticket, Member member);
  void checkIfTicketIsBooked(Ticket ticket);
  void checkMusicalDate(TicketExchangeRequest exchangeRequest);
  void checkOriginalSeatGrade(Ticket ticket, TicketExchangeRequest exchangeRequest);
}
