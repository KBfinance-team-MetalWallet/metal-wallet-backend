package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import org.springframework.data.domain.Page;

public interface TicketService {

  Page<TicketResponse> findAllBookedTickets(Long id, int page, int size);

  TicketResponse saveTicket(Member member, TicketRequest ticketRequest);

  void deleteTicket(Member member, long ticketId);

  void updateStatusChecked(long ticketId);

  TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);
}
