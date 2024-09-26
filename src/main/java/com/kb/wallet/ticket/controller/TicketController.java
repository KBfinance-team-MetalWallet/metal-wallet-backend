package com.kb.wallet.ticket.controller;

import com.google.zxing.WriterException;
import com.kb.wallet.global.common.response.ErrorResponse;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import com.kb.wallet.ticket.dto.request.TicketQrCreationRequest;
import com.kb.wallet.ticket.dto.response.TicketUsageResponse;
import com.kb.wallet.ticket.service.TicketService;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@Slf4j
public class TicketController {

  private final TicketService ticketService;

  @Autowired
  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @PostMapping
  public ResponseEntity<Ticket> createTicket(
      @AuthenticationPrincipal Member member,
      TicketDTO.TicketRequest ticketRequest) {
    Ticket ticket = ticketService.saveTicket(member, ticketRequest);
    return ResponseEntity.ok(ticket);
  }

  @GetMapping
  public ResponseEntity<Page<Ticket>> getUserTickets(
      @AuthenticationPrincipal Member member,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    Page<Ticket> tickets = ticketService.findAllUserTicket(member.getId(), page, size);
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/{ticketId}")
  public ResponseEntity<Ticket> getTicket(
      @PathVariable(name = "ticketId") long ticketId) {
    //TODO: MemberId 로그인 연동
//    Ticket ticket = ticketService.findTicket(member.getId(), ticketId);
    Ticket ticket = ticketService.findTicket(1L, ticketId);
    return ResponseEntity.ok(ticket);
  }

  @PostMapping("{ticketId}/qr")
  public ResponseEntity<String> generateQRCode(
      @PathVariable(name = "ticketId") long ticketId) throws IOException, WriterException {
    //TODO: MemberId 로그인 연동
    Long memberId = 1L;
    String qrCode = ticketService.generateTicketQRCode(memberId, ticketId);
    return ResponseEntity.ok(qrCode);
  }

  /*@GetMapping("/{ticketId}/use")
  public ResponseEntity<TicketUsageResponse> getTicketUsageStatus(
      //TODO: MemberId 로그인 연동
//      @AuthenticationPrincipal Member member,
      @PathVariable(name = "ticketId") long ticketId) {
//    TicketUsageResponse response = ticketService.isTicketUsed(member.getId(), ticketId);
    TicketUsageResponse response = ticketService.isTicketUsed(1L, ticketId);
    return ResponseEntity.ok(response);
  }*/

  @PutMapping("/{ticketId}/use")
  @PreAuthorize("hasRole('ADMIN')")
  // 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
  public ResponseEntity<?> updateTicket(@PathVariable(name = "ticketId") long ticketId) {
    Long memberId = 1L;
    //TODO: 비동기 HttpStatus 반환해야 함
    ticketService.checkTicket(memberId, ticketId).handle((result, ex) -> {
      if (ex != null) {
        Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;

        if (ex.getCause() instanceof CustomException) {
          log.info("ex.getCause()");
          log.info(String.valueOf(ex.getCause()));
          CustomException customException = (CustomException) cause;
          ErrorCode errorCode = customException.getErrorCode();
          return new ResponseEntity<>(ErrorResponse.of(errorCode, ex.getMessage()),
              HttpStatus.valueOf(errorCode.getStatus()));
//          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customException.getMessage());
        }
      }
      return result;
    });
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{ticketId}")
  public ResponseEntity<?> deleteTicket(
      @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.deleteTicket(member, ticketId);
    return ResponseEntity.ok().build();
  }
}
