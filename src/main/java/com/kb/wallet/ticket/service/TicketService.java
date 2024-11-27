package com.kb.wallet.ticket.service;

import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.EncryptRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.util.*;

public interface TicketService {

  Ticket getTicket(Long ticketId);

  List<TicketListResponse> getTickets(String email, TicketStatus ticketStatus, int page,
      int size, Long cursor);

  List<TicketResponse> bookTicket(String email, TicketRequest ticketRequest);

  void cancelTicket(String email, Long ticketId);

  void updateToCheckedStatus(VerifyTicketRequest request);

  ProposedEncryptResponse provideEncryptElement(Long ticketId, String email,
      EncryptRequest encryptRequest);
}