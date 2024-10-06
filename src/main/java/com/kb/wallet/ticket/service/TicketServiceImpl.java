package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_EXCHANGE_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.EXCHANGE_REQUESTED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import com.kb.wallet.ticket.dto.response.QrCreationResponse;
import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import javax.persistence.EntityNotFoundException;
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

  private final SignedTicketStorage signedTicketStorage;
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

  @Override

  public SignedTicketResponse signTicket(Long ticketId) {
    log.debug("Signing ticket with ID: {}", ticketId);

    try {
      Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ERROR, "티켓을 찾을 수 없습니다."));

      TicketResponse response = TicketResponse.toTicketResponse(ticket);
      String ticketInfo = generateTicketData(response);

      log.debug("Generated ticket data for signing: {}", ticketInfo);

      // 티켓 정보 암호화
      PublicKey publicKey = rsaService.getPublicKey();
      String encryptedTicketInfo = rsaService.encrypt(ticketInfo, publicKey);

      log.debug("Encrypted ticket info successfully");

      // 암호화된 티켓 정보 서명
      PrivateKey privateKey = rsaService.getPrivateKey();
      String signature = rsaService.sign(encryptedTicketInfo, privateKey);

      log.debug("Generated signature for ticket");

      return SignedTicketResponse.builder()
        .encryptedTicketInfo(encryptedTicketInfo)
        .signature(signature)
        .build();
    } catch (CustomException e) {
      log.error("Custom error occurred while signing ticket: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error occurred while signing ticket", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "티켓 서명 중 오류가 발생했습니다.");
    }
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

  @Override
  public boolean verifyTicketSignature(SignedTicketResponse signedTicketResponse,
    String signature,
    String deviceId) throws Exception {
    PublicKey publicKey = rsaService.getPublicKey();
    String encryptedTicketInfo = signedTicketResponse.getEncryptedTicketInfo();

    if (!rsaService.verify(encryptedTicketInfo, signature, publicKey)) {
      log.warn("Signature verification failed for ticket");
      return false; // 서명 검증 실패 시 false 반환
    }

    PrivateKey privateKey = rsaService.getPrivateKey();
    String decryptedTicketInfo = rsaService.decrypt(encryptedTicketInfo, privateKey);
    TicketResponse decryptedTicketResponse = convertStringToTicketResponse(decryptedTicketInfo);

    // deviceId 검증
    if (!decryptedTicketResponse.getDeviceId().equals(deviceId)) {
      log.warn("Device ID mismatch");
      return false;
    }

    Ticket savedTicket = ticketRepository.findById(decryptedTicketResponse.getId())
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ERROR, "티켓을 찾을 수 없습니다."));

    // 티켓 가용성 확인
    //TODO: isTicketAvailable paran 변경 TicketUseValidationResponse 생성해서
    if (!isTicketAvailable(
      savedTicket)) {// TicketUseValidationResponse.toTicketUseValidationResponse(savedTicket);
      log.warn("Ticket is not available for use. Ticket ID: {}", savedTicket.getId());
      return false;
    }

    log.info("Ticket verification successful. Ticket ID: {}", savedTicket.getId());
    return true;

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
  public Page<TicketResponse> findAllBookedTickets(String email, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Ticket> ticketsByMemberIdAndTicketStatus =
      ticketRepository.findTicketsByMemberAndTicketStatus(email, TicketStatus.BOOKED, pageable);
    return ticketsByMemberIdAndTicketStatus.map(TicketResponse::toTicketResponse);
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
  public QrCreationResponse generateQRCodeData(String email, Long ticketId, String deviceId)
    throws Exception {
//    log.info("Generating QR code for ticket ID: {}, Member ID: {}", ticketId, member.getId());
    Member member = memberService.getMemberByEmail(email);
    if (member.getId() == null) {
      throw new CustomException(ErrorCode.MEMBER_STATUS_INVALID, "유효하지 않은 회원 정보입니다.");
    }
    try {
      TicketResponse ticket = findTicket(member.getEmail(), ticketId);
      //TODO: isTicketAvailable param 변경, TicketUseValidationResponse response
      if (!isTicketAvailable(ticket)) {
        throw new CustomException(ErrorCode.TICKET_STATUS_INVALID,
          "티켓 상태가 유효하지 않습니다.");
      }
      // deviceId 유효성 검사
      if (deviceId == null || deviceId.isEmpty()) {
        throw new CustomException(ErrorCode.NOT_VALID_ERROR, "디바이스 ID는 필수입니다.");
      }

      String token = tokenProvider.createToken(ticketId);
      KeyPair keyPair = rsaService.generateKeyPair();
      PublicKey publicKey = keyPair.getPublic();
      PrivateKey privateKey = keyPair.getPrivate();

      String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
      String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

      long expirationTime = System.currentTimeMillis() + 30000; // 30초 후
      String plaintext = String.format("%d|%d|%s", ticketId, expirationTime, deviceId);
      String encryptedData = rsaService.encrypt(plaintext, publicKey);

      savePrivateKey(ticketId, privateKeyString);
      // QR 코드 데이터 생성 (encryptedData를 사용)
      String qrCodeData = generateQRCodeData(encryptedData);

      return QrCreationResponse.toQrCreationResponse(token, encryptedData, 300, privateKeyString);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("QR 코드 생성 중 오류가 발생했습니다.", e);
      throw new CustomException(ErrorCode.QR_CODE_GENERATION_ERROR,
        "QR 코드 생성 중 오류가 발생했습니다.");
    }

  }

  private String generateQRCodeData(String encryptedData) {
    try {
      // QR 코드 생성
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(encryptedData, BarcodeFormat.QR_CODE, 200, 200);

      // QR 코드를 이미지로 변환
      ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
      byte[] pngData = pngOutputStream.toByteArray();

      // 이미지를 Base64로 인코딩
      return encryptedData;
    } catch (WriterException | IOException e) {
      log.error("QR 코드 생성 중 오류 발생", e);
      throw new CustomException(ErrorCode.QR_CODE_GENERATION_ERROR, "QR 코드 생성 중 오류가 발생했습니다.");
    }
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


  @Override
  @Transactional("jpaTransactionManager")
  public TicketResponse useTicket(Member member, Long ticketId)
    throws Exception {
    Ticket ticket = ticketRepository.findByIdAndMember(ticketId, member)
      .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

    if (ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new IllegalStateException("Ticket is not in a valid state for use");
    }

    // 티켓 상태 업데이트
    ticket.setTicketStatus(TicketStatus.CHECKED);
    Ticket updatedTicket = ticketRepository.save(ticket);

    return TicketResponse.toTicketResponse(updatedTicket);
  }
}
