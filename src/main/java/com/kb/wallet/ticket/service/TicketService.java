package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.qrcode.dto.request.DecryptionRequest;
import com.kb.wallet.qrcode.dto.response.DecryptionResponse;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.QrCreationResponse;
import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TicketService {

  TicketResponse findTicket(String email, Long ticketId);

  Ticket findTicketById(Long id);


  List<TicketListResponse> findAllBookedTickets(String email, TicketStatus ticketStatus, int page,
      int size, Long cursor);

  List<TicketResponse> saveTicket(String email, TicketRequest ticketRequest, String deviceId);

  void cancelTicket(String email, Long ticketId);

  boolean isTicketAvailable(Long memberId, TicketResponse ticket);

  void updateStatusChecked(Ticket ticket);

  TicketExchangeResponse createTicketExchange(Member member,
    TicketExchangeRequest exchangeRequest);

  void cancelTicketExchange(String email, Long ticketId);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);

  DecryptionResponse useTicket(Member member, DecryptionRequest decryptionRequest) throws Exception;

  QrCreationResponse generateQRCodeData(String email, Long ticketId) throws Exception;

  void savePrivateKey(Long ticketId, String privateKey);

  SignedTicketResponse signTicket(Long ticketId) throws Exception;

  boolean verifyTicketSignature(TicketResponse ticket, String signature, String deviceId)
    throws Exception;

}