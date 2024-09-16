package com.kb.wallet.ticket.controller;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDto;
import com.kb.wallet.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

//  @PostMapping("")
//  public ResponseEntity<Ticket> saveTicket(
//      @AuthenticationPrincipal Member member,
//      TicketDto ticketRequest
//  ) {
//    Ticket ticket = ticketService.saveTicket(member, ticketRequest);
//    return ResponseEntity.ok(ticket);
//  }

  @PostMapping("")
  public ResponseEntity<Ticket> saveTicket(
      TicketDto ticketRequest
  ) {
    Member member = new Member();
    member.setId(1L);
    Ticket ticket = ticketService.saveTicket(member, ticketRequest);
    return ResponseEntity.ok(ticket);
  }

  @GetMapping("")
  public ResponseEntity<Page<Ticket>> getUserTickets(
      @AuthenticationPrincipal Member member,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<Ticket> tickets = ticketService.getUserTickets(member.getId(), page, size);
    return ResponseEntity.ok(tickets);
  }

}
