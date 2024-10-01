package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TicketService {

  Ticket findTicket(Long memberId, Long ticketId);

  Ticket findTicketById(Long id);

  Page<TicketResponse> findAllBookedTickets(Long id, int page, int size);

  List<TicketResponse> saveTicket(String email, TicketRequest ticketRequest);


  void deleteTicket(Member member, long ticketId);

  boolean isTicketAvailable(Long memberId, Ticket ticket);

  void updateStatusChecked(Ticket ticket);

  TicketExchangeResponse createTicketExchange(Member member,
    TicketExchangeRequest exchangeRequest);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);
}
