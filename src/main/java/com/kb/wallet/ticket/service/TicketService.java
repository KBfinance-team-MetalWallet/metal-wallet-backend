package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import org.springframework.data.domain.Page;

public interface TicketService {

  Page<CreateTicketResponse> findAllBookedTickets(Long id, int page, int size);

  CreateTicketResponse saveTicket(Member member, CreateTicketRequest ticketRequest);

  void deleteTicket(Member member, long ticketId);

  void checkTicket(long ticketId);
}
