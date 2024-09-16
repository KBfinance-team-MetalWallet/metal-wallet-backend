package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDto;
import org.springframework.data.domain.Page;

public interface TicketService {
  Page<Ticket> getUserTickets(Long id, int page, int size);

  Ticket saveTicket(Member member, TicketDto ticketRequest);
}
