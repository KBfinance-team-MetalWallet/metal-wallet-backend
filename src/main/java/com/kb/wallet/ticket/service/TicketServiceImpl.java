package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_EXCHANGE_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.BOOKED;
import static com.kb.wallet.ticket.constant.TicketStatus.EXCHANGE_REQUESTED;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketInfo;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketCheckService ticketCheckService;
  private final TicketRepository ticketRepository;
  private final TicketExchangeRepository ticketExchangeRepository;
  private final MemberService memberService;
  private final SeatService seatService;
  private final RSAService rsaService;

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public List<TicketResponse> saveTicket(String email, TicketRequest ticketRequest) {
    if (ticketRequest.getSeatId() == null || ticketRequest.getSeatId().isEmpty()) {
      throw new CustomException(ErrorCode.NOT_VALID_ERROR, "좌석 ID는 필수입니다.");
    }
    if (ticketRequest.getDeviceId() == null || ticketRequest.getDeviceId().isEmpty()) {
      throw new CustomException(ErrorCode.NOT_VALID_ERROR, "디바이스 ID는 필수입니다.");
    }
    Member member = memberService.getMemberByEmail(email);
    List<TicketResponse> responses = new ArrayList<>();
    for (Long seatId : ticketRequest.getSeatId()) {
      Seat seat = seatService.getSeatById(seatId);
      seatService.checkSeatAvailability(seat);

      Ticket bookedTicket = Ticket.createBookedTicket(member, seat.getSchedule().getMusical(),
          seat);
      bookedTicket.setDeviceId(ticketRequest.getDeviceId());

      Ticket savedTicket = ticketRepository.save(bookedTicket);
      responses.add(TicketResponse.toTicketResponse(savedTicket));

      seat.markAsUnavailable();
      seat.getSection().decrementAvailableSeats();
    }
    return responses;
  }

  @Override
  public ProposedEncryptResponse provideEncryptElement(Long ticketId, String email) {
    Ticket ticket = findTicketById(ticketId);
    TicketInfo ticketInfo = TicketInfo.fromTicket(ticket);

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
    response.setTicketInfo(ticketInfo);

    long expirationTime = System.currentTimeMillis() + 31000;
    response.setSeconds(expirationTime / 1000);

    log.info("PublicKey : {}", publicKeyString);
    return response;
  }

  @Override
  public TicketResponse findTicket(String email, Long ticketId) {
    Member member = memberService.getMemberByEmail(email);
    Ticket ticket = ticketRepository.findByIdAndMember(ticketId, member)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
    return TicketResponse.toTicketResponse(ticket);
  }

  @Override
  public List<TicketListResponse> findAllBookedTickets(String email, TicketStatus ticketStatus,
      int page, int size, Long cursor) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberAndTicketStatus(email, ticketStatus, cursor,
        pageable);
  }

  public void updateStatusChecked(Ticket ticket) {
    ticket.setTicketStatus(TicketStatus.CHECKED);
  }

  @Override
  public void cancelTicket(String email, Long ticketId) {
    Ticket ticket = ticketRepository.findByMember(ticketId, email)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
    if (!ticket.isCancellable()) {
      throw new CustomException(TICKET_STATUS_INVALID);
    }
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }

  @Override
  public void cancelTicketExchange(String email, Long ticketId) {
    Ticket ticket = ticketRepository.findByMember(ticketId, email)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));

    if (ticket.isExchangeRequested()) {
      TicketExchange ticketExchange = ticketExchangeRepository.findByTicketId(ticketId)
          .orElseThrow(() -> new CustomException(TICKET_EXCHANGE_NOT_FOUND_ERROR));

      ticket.setTicketStatus(TicketStatus.BOOKED);
      ticketRepository.save(ticket);

      ticketExchangeRepository.delete(ticketExchange);
    } else {
      throw new CustomException(TICKET_STATUS_INVALID);
    }
  }

  @Override
  public Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<TicketExchange> ticketExchanges = ticketExchangeRepository.findByTicketMember(member,
        pageable);
    return ticketExchanges.map(TicketExchangeResponse::createTicketExchangeResponse);
  }

  @Override
  public TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest) {
    Ticket ticket = findTicketById(exchangeRequest.getTicketId());

    ticketCheckService.checkTicketOwner(ticket, member);
    ticketCheckService.checkIfTicketIsBooked(ticket);
    ticketCheckService.checkMusicalDate(exchangeRequest);
    ticketCheckService.checkOriginalSeatGrade(ticket, exchangeRequest);

    TicketExchange ticketExchange = TicketExchange.toTicketExchange(ticket, exchangeRequest);
    ticketExchangeRepository.save(ticketExchange);

    ticket.setTicketStatus(EXCHANGE_REQUESTED);
    ticketRepository.save(ticket);

    return TicketExchangeResponse.createTicketExchangeResponse(ticketExchange);
  }

  @Override
  public Ticket findTicketById(Long id) {
    return ticketRepository.findById(id)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
  }

  @Override
  public void updateToCheckedStatus(VerifyTicketRequest request) {
    try {
      String decryptedData = rsaService.decrypt(request.getEncryptedTicketInfo(),
          rsaService.getPrivateKey());
      JSONObject ticketInfo = new JSONObject(decryptedData).getJSONObject("ticketInfo");

      String extractedDeviceId = ticketInfo.getString("deviceId");
      Long extractedId = ticketInfo.getLong("ticketId");

      Ticket ticket = findTicketById(extractedId);
      if (!ticket.getTicketStatus().equals(BOOKED) || !ticket.getDeviceId()
          .equals(extractedDeviceId)) {
        throw new CustomException(ErrorCode.TICKET_STATUS_INVALID);
      }

      updateStatusChecked(ticket);
      ticketRepository.save(ticket);

    } catch (Exception e) {
      log.error("티켓 상태를 CHECKED로 업데이트하지 못했습니다", e);
      throw new CustomException(ErrorCode.TICKET_UPDATE_ERROR);
    }
  }
}