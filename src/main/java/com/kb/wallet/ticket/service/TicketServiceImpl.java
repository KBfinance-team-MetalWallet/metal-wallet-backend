package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDto;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService{

  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;

  @Autowired
  public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
    this.ticketRepository = ticketRepository;
    this.ticketMapper = ticketMapper;
  }

  @Override
  public Ticket saveTicket(Member member, TicketDto ticketRequest) {
    Ticket ticket = Ticket.builder()
        .member(member)
        .ticketStatus(TicketStatus.BOOKED)
        .build();
    ticketRepository.save(ticket);
    return ticket;
  }

  @Override
  public Page<Ticket> getUserTickets(Long id, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberId(id, pageable);
  }

}
