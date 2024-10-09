package com.kb.wallet.ticket.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.dto.request.SignedTicketRequest;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.service.TicketService;
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
    List<TicketResponse> tickets = ticketService.saveTicket(member.getEmail(), ticketRequest
    );
    return ApiResponse.created(tickets);
  }

  @PostMapping("/sign")
  public ResponseEntity<SignedTicketResponse> signTicket(@RequestBody SignedTicketRequest request)
    throws Exception {
    SignedTicketResponse signedTicket = ticketService.signTicket(request.getTicketId());
    return ResponseEntity.ok(signedTicket);
  }

  @GetMapping
  public ApiResponse<CursorResponse<TicketListResponse>> getUserTickets(
      @AuthenticationPrincipal Member member,
      @RequestParam(name = "cursor", required = false) Long cursor,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "status", required = false) String status) {
    TicketStatus ticketStatus = "booked".equalsIgnoreCase(status) ? TicketStatus.BOOKED : null;
    List<TicketListResponse> tickets;
    Long nextCursor = null;

    tickets = ticketService.findAllBookedTickets(member.getEmail(),
        ticketStatus, 0,
        size, cursor);
    if(!tickets.isEmpty()) {
      nextCursor = tickets.get(tickets.size() - 1).getId();
    }
    CursorResponse<TicketListResponse> cursorResponse = new CursorResponse<>(tickets, nextCursor);
    return ApiResponse.ok(cursorResponse);
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<TicketResponse> getTicket(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId) {
    TicketResponse response = ticketService.findTicket(member.getEmail(), ticketId);
    System.out.println("**********************" + "ticketId = " + ticketId);
    return ApiResponse.ok(response);
  }

  @PostMapping("encrypt/{ticketId}")
  public ResponseEntity<ProposedEncryptResponse> generateEncryptData(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId) {
    ProposedEncryptResponse response = ticketService.provideEncryptElement(ticketId,
      member.getEmail());
    return ResponseEntity.ok(response);
  }

  @PutMapping("/use")
// TODO : @PreAuthorize("hasRole('ADMIN')")
// TODO : 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
  public ResponseEntity<Void> updateTicket(
    @AuthenticationPrincipal Member member,
    @RequestBody VerifyTicketRequest request
  ) {
    ticketService.updateToCheckedStatus(request);
    return ResponseEntity.ok().build();
  }


  @DeleteMapping("/{ticketId}")
  public ApiResponse<Void> cancelTicket(
    @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.cancelTicket(member.getEmail(), ticketId);
    return ApiResponse.ok();
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

  @DeleteMapping("/exchange/{ticketId}")
  public ApiResponse<Void> cancelTicketExchange(
    @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.cancelTicketExchange(member.getEmail(), ticketId);
    return ApiResponse.ok();
  }
}
