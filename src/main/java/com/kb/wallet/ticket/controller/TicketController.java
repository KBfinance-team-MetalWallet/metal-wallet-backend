package com.kb.wallet.ticket.controller;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class TicketController {

  private final TicketService ticketService;

  @Autowired
  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @PostMapping
  public ResponseEntity<TicketResponse> createTicket(
      @AuthenticationPrincipal Member member,
      @RequestBody TicketRequest ticketRequest) {
    TicketResponse ticket = ticketService.saveTicket(member, ticketRequest);
    return ResponseEntity.ok(ticket);
  }

  @GetMapping
  public ResponseEntity<Page<TicketResponse>> getUserTickets(
      @AuthenticationPrincipal Member member,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    Page<TicketResponse> tickets = ticketService.findAllBookedTickets(member.getId(), page, size);
    return ResponseEntity.ok(tickets);
  }

  @PutMapping("/{ticketId}/use")
  @PreAuthorize("hasRole('ADMIN')")
  // 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
  public ResponseEntity<?> updateTicket(@PathVariable(name = "ticketId") long ticketId) {
    ticketService.checkTicket(ticketId);
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
