package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.EncryptRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepository;
  private final MemberService memberService;
  private final SeatService seatService;
  private final RSAService rsaService;

  @Override
  public Ticket getTicket(Long id) {
    return ticketRepository.findById(id)
      .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
  }

  @Override
  public List<TicketListResponse> getTickets(String email, TicketStatus ticketStatus,
    int page, int size, Long cursor) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findAllByMemberAndTicketStatus(email, ticketStatus, cursor,
      pageable);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public List<TicketResponse> bookTicket(String email, TicketRequest ticketRequest) {
    Member member = memberService.getMemberByEmail(email);

    List<TicketResponse> responses = new ArrayList<>();

    for (Long seatId : ticketRequest.getSeatId()) {
      Ticket bookedTicket = bookTicketForSeat(seatId, ticketRequest.getDeviceId(), member);
      responses.add(TicketResponse.toTicketResponse(bookedTicket));
    }
    return responses;
  }

  private Ticket bookTicketForSeat(Long seatId, String deviceId, Member member) {
    Seat seat = seatService.getSeatById(seatId);
    seat.checkSeatAvailability();

    Ticket ticket = saveTicket(member, seat, deviceId);

    seat.updateSeatAvailability();

    return ticket;
  }

  private Ticket saveTicket(Member member, Seat seat, String deviceId) {
    Ticket ticket = Ticket.createBookedTicket(member, seat.getSchedule().getMusical(), seat,
        deviceId);
    return ticketRepository.save(ticket);
  }

  @Override
  public ProposedEncryptResponse provideEncryptElement(Long ticketId, String email,
    EncryptRequest encryptRequest) {
    encryptRequest.validateDeviceId();

    Ticket ticket = getTicket(ticketId);
    ticket.isBooked();

    PublicKey publicKey;
    try {
      publicKey = rsaService.getPublicKey();
    } catch (Exception e) {
      log.error("Failed to retrieve public key", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "공개 키 조회 중 오류가 발생했습니다.");
    }

    ProposedEncryptResponse response = new ProposedEncryptResponse();
    String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    response.setPublicKey(publicKeyString);

    long expirationTime = System.currentTimeMillis() + 31000;
    response.setSeconds(expirationTime / 1000);

    return response;
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void cancelTicket(String email, Long ticketId) {
    Ticket ticket = ticketRepository.findByTicketIdAndEmail(ticketId, email)
      .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
    ticket.isCancellable();
    updateTicketStatus(ticket, TicketStatus.CANCELED);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void updateToCheckedStatus(VerifyTicketRequest request) {
    String decryptedData = rsaService.decrypt(request.getEncryptedTicketInfo());
    JSONObject ticketInfo = new JSONObject(decryptedData).getJSONObject("ticketInfo");

    String extractedDeviceId = ticketInfo.getString("deviceId");
    Long extractedId = ticketInfo.getLong("ticketId");

    Ticket ticket = getTicket(extractedId);
    ticket.validateCheckedChange(extractedDeviceId);
    updateTicketStatus(ticket, TicketStatus.CHECKED);
  }

  private void updateTicketStatus(Ticket ticket, TicketStatus status) {
    ticket.updateTicketStatus(status);
    ticketRepository.save(ticket);
  }
}
