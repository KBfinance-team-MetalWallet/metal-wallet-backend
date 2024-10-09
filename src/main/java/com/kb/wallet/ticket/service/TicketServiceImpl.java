package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_EXCHANGE_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.EXCHANGE_REQUESTED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.qrcode.service.RSAService;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final TicketMapper ticketMapper;

  private final TokenProvider tokenProvider;
  private final RSAService rsaService;
  private final Map<Long, String> privateKeyStorage = new ConcurrentHashMap<>();

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
      bookedTicket.setDeviceId(ticketRequest.getDeviceId()); // deviceId를 그대로 저장

      Ticket savedTicket = ticketRepository.save(bookedTicket);
      responses.add(TicketResponse.toTicketResponse(savedTicket));

      seat.markAsUnavailable();
      seat.getSection().decrementAvailableSeats();
    }

    return responses;
  }

  private String generateTicketData(TicketResponse response) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      log.error("Error generating ticket data", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "티켓 데이터 생성 중 오류가 발생했습니다.");
    }
  }

  private TicketResponse convertStringToTicketResponse(String ticketInfo) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(ticketInfo, TicketResponse.class);
    } catch (JsonProcessingException e) {
      log.error("Error converting ticket info to TicketResponse", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "티켓 정보 변환 중 오류가 발생했습니다.");
    }
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
  public Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page,
      int size) {
    member = Member.builder()
        .id(1L)
        .build();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<TicketExchange> ticketExchanges = ticketExchangeRepository.findByTicketMember(member,
        pageable);
    return ticketExchanges.map(TicketExchangeResponse::createTicketExchangeResponse);
  }

  @Override
  public TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest) {
    member = Member.builder()
        .id(1L)
        .build();
    Ticket ticket = findTicketById(exchangeRequest.getTicketId());

    ticketCheckService.checkTicketOwner(ticket, member);
    ticketCheckService.checkIfTicketIsBooked(ticket);
    ticketCheckService.checkMusicalDate(exchangeRequest);
    ticketCheckService.checkOriginalSeatGrade(ticket, exchangeRequest);

    // TODO : 티켓 교환 알고리즘 작성해야 함 .

    TicketExchange ticketExchange = TicketExchange.toTicketExchange(ticket, exchangeRequest);
    ticketExchangeRepository.save(ticketExchange);

    // 신청 대상인 기존 티켓 상태 변경
    ticket.setTicketStatus(EXCHANGE_REQUESTED);
    ticketRepository.save(ticket);

    return TicketExchangeResponse.createTicketExchangeResponse(ticketExchange);
  }

  @Override
  public Ticket findTicketById(Long id) {
    return ticketRepository.findById(id)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
  }

  public boolean isTicketAvailable(TicketResponse ticket) {
    // 티켓이 null이 아니고, 상태가 BOOKED인지 확인
    if (ticket == null || ticket.getTicketStatus() != TicketStatus.BOOKED) {
      return false;
    }

    // 티켓의 유효 기간이 지나지 않았는지 확인
    try {
      LocalDateTime validUntil = LocalDateTime.parse(ticket.getValidUntil());
      if (LocalDateTime.now().isAfter(validUntil)) {
        return false;
      }
    } catch (DateTimeParseException e) {
      // 날짜 파싱 오류 처리
      log.error("Error parsing validUntil date for ticket: {}", ticket.getId(), e);
      return false;
    }

    // 추가적인 검증 로직이 필요하다면 여기에 구현

    return true;
  }
}
