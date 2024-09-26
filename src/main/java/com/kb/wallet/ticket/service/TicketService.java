package com.kb.wallet.ticket.service;

import com.google.zxing.WriterException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import com.kb.wallet.ticket.dto.response.TicketUsageResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;

public interface TicketService {
  Ticket findTicket(Long memberId, Long ticketId);
  Page<Ticket> findAllUserTicket(Long id, int page, int size);

  Ticket saveTicket(Member member, TicketDTO.TicketRequest ticketRequest);

  String generateTicketQRCode(Long memberId, Long ticketId) throws IOException, WriterException;

  void deleteTicket(Member member, long ticketId);

  CompletableFuture<Void> checkTicket(Long memberId, Long ticketId);
}
