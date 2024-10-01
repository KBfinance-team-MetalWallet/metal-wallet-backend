package com.kb.wallet.ticket.controller;

import com.google.zxing.WriterException;
import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.service.TicketService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

  private final TicketService ticketService;
  private final MemberService memberService;
  @Lazy
  private final TokenProvider tokenProvider;

  @PostMapping
  public ApiResponse<List<TicketResponse>> createTicket(
    @AuthenticationPrincipal Member member,
    @RequestBody TicketRequest ticketRequest) {
    List<TicketResponse> tickets = ticketService.saveTicket(member.getEmail(), ticketRequest);
    return ApiResponse.created(tickets);
  }

  @GetMapping
  public ResponseEntity<Page<TicketResponse>> getUserTickets(
    @AuthenticationPrincipal Member member,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size) {
    Page<TicketResponse> tickets = ticketService.findAllBookedTickets(member.getId(), page, size);
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/{ticketId}")
  public ResponseEntity<Ticket> getTicket(
    @PathVariable(name = "ticketId") Long ticketId) {
    //TODO: MemberId 로그인 연동
//    Ticket ticket = ticketService.findTicket(member.getId(), ticketId);
    Ticket ticket = ticketService.findTicket(1L, ticketId);
    return ResponseEntity.ok(ticket);
  }

  @PostMapping("{ticketId}/qr")
  public void generateQRCode(
//      public ResponseEntity<QrCreationResponse> generateQRCode (
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId) throws IOException, WriterException {

    String token = tokenProvider.createToken(ticketId);
    Member loginedMemeber = memberService.getMemberByEmail(member.getEmail());

    //TODO: byte[] -> String으로 변환할 것
    //  String encryptedData = util.encrypt()

    //TODO: qr 생성은 클라이언트에서 처리하도록 변경할 것

    //TODO: qrCreationResponse로 반환해야함 = qrCreationResponse
    //  이 DTO에는 token, qrBytes, secord값이 담김
//    return ResponseEntity.ok(qrCreationResponse);
  }

  @PutMapping("/use")
//  @PreAuthorize("hasRole('ADMIN')")
  // 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
  public ResponseEntity<?> updateTicket(
    @AuthenticationPrincipal Member member) {
    //TODO: QrCreationResponse qrCreationResponse 를 request로 받는다
    //  복호화
    //  qr 해서 받는 데이터 token, qrBytes, second
    //  qrBytes 디코딩 -> 예약자의 memberId, 티켓 ID
    //TODO: 동시성 처리
    Ticket ticket = ticketService.findTicketById(2L);

    if (ticketService.isTicketAvailable(2L, ticket)) {
      ticketService.updateStatusChecked(ticket);
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{ticketId}")
  public ResponseEntity<?> deleteTicket(
    @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.deleteTicket(member, ticketId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/exchange")
  public ResponseEntity<Page<TicketExchangeResponse>> getUserExchangedTickets(
    @AuthenticationPrincipal Member member,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    Page<TicketExchangeResponse> userExchangedTickets = ticketService.getUserExchangedTickets(
      member, page, size);
    return ResponseEntity.ok(userExchangedTickets);
  }

  @PostMapping("/exchange")
  public ResponseEntity<TicketExchangeResponse> createTicketExchange(
    @AuthenticationPrincipal Member member,
    @RequestBody TicketExchangeRequest exchangeRequest) {
    TicketExchangeResponse ticketExchange = ticketService.createTicketExchange(member,
      exchangeRequest);
    return ResponseEntity.ok(ticketExchange);
  }
}
