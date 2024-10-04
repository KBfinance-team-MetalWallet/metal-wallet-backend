package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_EXCHANGE_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.EXCHANGE_REQUESTED;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.qrcode.dto.request.DecryptionRequest;
import com.kb.wallet.qrcode.dto.response.DecryptionResponse;
import com.kb.wallet.qrcode.service.RSAService;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.QrCreationResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.security.KeyPair;
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
    Member member = memberService.getMemberByEmail(email);
    List<TicketResponse> responses = new ArrayList<>();

    for (Long seatId : ticketRequest.getSeatId()) {
      Seat seat = seatService.getSeatById(seatId);

      // 이미 예약된 좌석인지 체크
      seatService.checkSeatAvailability(seat);

      // 티켓 엔티티 생성
      Ticket bookedTicket = Ticket.createBookedTicket(member, seat.getSchedule().getMusical(),
          seat);

      // 티켓 저장
      Ticket savedTicket = ticketRepository.save(bookedTicket);

      // 티켓 응답 추가
      responses.add(TicketResponse.toTicketResponse(savedTicket));

      // 해당 좌석은 더 이상 예약할 수 없도록 설정
      seat.markAsUnavailable();

      // 구역별 예약 가능 좌석 수 업데이트
      seat.getSection().decrementAvailableSeats();
    }

    return responses;
  }

  @Override
  public TicketResponse findTicket(String email, Long ticketId) {
    Member member = memberService.getMemberByEmail(email);
    Ticket ticket = ticketRepository.findByIdAndMember(ticketId, member)
        .orElseThrow(() -> new CustomException(TICKET_NOT_FOUND_ERROR));
    return TicketResponse.toTicketResponse(ticket);
  }

  @Override
  public Page<TicketListResponse> findAllBookedTickets(String email, TicketStatus ticketStatus,
      int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberAndTicketStatus(email, ticketStatus, pageable);
  }

  public void updateStatusChecked(Ticket ticket) {
    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
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

  @Override
  public void savePrivateKey(Long ticketId, String privateKey) {
    privateKeyStorage.put(ticketId, privateKey);
  }

  @Override
  public String getPrivateKey(Long ticketId) {
    return privateKeyStorage.get(ticketId);
  }

  @Override
  public QrCreationResponse generateQRCodeData(String email, Long ticketId) throws Exception {
//    log.info("Generating QR code for ticket ID: {}, Member ID: {}", ticketId, member.getId());
    Member member = memberService.getMemberByEmail(email);
    if (member.getId() == null) {
      throw new CustomException(ErrorCode.MEMBER_STATUS_INVALID, "유효하지 않은 회원 정보입니다.");
    }
    try {
      TicketResponse ticket = findTicket(member.getEmail(), ticketId);
      if (!isTicketAvailable(member.getId(), ticket)) {
        throw new CustomException(ErrorCode.TICKET_STATUS_INVALID,
            "티켓 상태가 유효하지 않습니다.");
      }

      String token = tokenProvider.createToken(ticketId);
      KeyPair keyPair = rsaService.generateKeyPair();
      PublicKey publicKey = keyPair.getPublic();
      PrivateKey privateKey = keyPair.getPrivate();

      String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
      String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

      long expirationTime = System.currentTimeMillis() + 300000; // 5분 후
      String plaintext = String.format("%d|%d", ticketId, expirationTime);
      String encryptedData = rsaService.encrypt(plaintext, publicKeyString);

      savePrivateKey(ticketId, privateKeyString);

//      return QrCreationResponse.builder()
//          .token(token)
//          .encodedTicketInfo(encryptedData)
//          .second(300)
//          .privateKey(privateKey)
//          .build();
      return QrCreationResponse.toQrCreationResponse(token, encryptedData, 300, privateKeyString);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("QR 코드 생성 중 오류가 발생했습니다.", e);
      throw new CustomException(ErrorCode.QR_CODE_GENERATION_ERROR,
          "QR 코드 생성 중 오류가 발생했습니다.");
    }

  }

  public boolean isTicketAvailable(Long memberId, TicketResponse ticket) {
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


  @Override
  @Transactional("jpaTransactionManager")
  public DecryptionResponse useTicket(Member member, DecryptionRequest decryptionRequest)
      throws Exception {
    String encryptedText = decryptionRequest.getEncryptedText();

    // RSA 복호화
    String decryptedText = rsaService.decrypt(encryptedText, decryptionRequest.getPrivateKey());
    // 복호화된 텍스트에서 티켓 ID와 만료 시간 추출
    String[] parts = decryptedText.split("\\|");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid decrypted ticket information");
    }
    Long ticketId = Long.parseLong(parts[0]);
    long expirationTime = Long.parseLong(parts[1]);

    System.out.println("expirationTime = " + expirationTime + "**************************");
    System.out.println(
        "*************************ticketId = " + ticketId + "**************************");

    // 만료 시간 체크
    if (System.currentTimeMillis() > expirationTime) {
      throw new IllegalStateException("QR code has expired");
    }
    // 티켓 사용 로직 실행
    TicketResponse ticketResponse = findTicket(member.getEmail(), ticketId);
    if (!isTicketAvailable(member.getId(), ticketResponse)) {
      throw new CustomException(TICKET_STATUS_INVALID, "티켓 상태가 유효하지 않습니다.");
    }
    Ticket ticket = findTicketById(ticketId);
    updateStatusChecked(ticket);
    // DecryptionResponse 객체 생성 및 반환
    return new DecryptionResponse(decryptedText);
  }

}
