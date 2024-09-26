package com.kb.wallet.ticket.service;

import com.google.zxing.WriterException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;

public interface TicketService {
  Ticket findTicket(Long memberId, Long ticketId);
  Page<TicketResponse> findAllBookedTickets(Long id, int page, int size);

  TicketResponse saveTicket(Member member, TicketRequest ticketRequest);

  String generateTicketQRCode(Long memberId, Long ticketId) throws IOException, WriterException;

  void deleteTicket(Member member, long ticketId);

  CompletableFuture<Void> updateStatusChecked(Long memberId, Long ticketId);
  TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);
}
