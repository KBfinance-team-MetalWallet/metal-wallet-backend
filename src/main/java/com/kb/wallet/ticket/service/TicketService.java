package com.kb.wallet.ticket.service;

import com.google.zxing.WriterException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.*;
import com.kb.wallet.ticket.dto.response.*;
import java.io.IOException;
import org.springframework.data.domain.Page;

public interface TicketService {
  Ticket findTicket(Long memberId, Long ticketId);
  Ticket findTicketById(Long id);
  Page<TicketResponse> findAllBookedTickets(Long id, int page, int size);

  TicketResponse saveTicket(Member member, TicketRequest ticketRequest);

  byte[] generateTicketQRCode(String email, Long ticketId) throws IOException, WriterException;

  void deleteTicket(Member member, long ticketId);
  boolean isTicketAvailable(Long memberId, Ticket ticket);
  void updateStatusChecked(Ticket ticket);
  TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);
}
