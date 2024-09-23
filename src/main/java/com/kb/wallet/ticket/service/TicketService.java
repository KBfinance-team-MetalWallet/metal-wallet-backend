package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.dto.request.CreateTicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.CreateTicketRequest;
import com.kb.wallet.ticket.dto.response.CreateTicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.CreateTicketResponse;
import org.springframework.data.domain.Page;

public interface TicketService {

  Page<CreateTicketResponse> findAllBookedTickets(Long id, int page, int size);

  CreateTicketResponse saveTicket(Member member, CreateTicketRequest ticketRequest);

  void deleteTicket(Member member, long ticketId);

  void checkTicket(long ticketId);

  CreateTicketExchangeResponse createTicketExchange(Member member,
      CreateTicketExchangeRequest exchangeRequest);
}
