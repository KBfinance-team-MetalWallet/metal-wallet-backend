package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
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
  public Ticket saveTicket(Member member, TicketDTO.TicketRequest ticketRequest) {
    // 뮤지컬 테이블에서 뮤지컬 찾고
    // 일정 테이블에서 일정 찾아서 넣어줘야 함.
    // 임의 member 생성.. 필터에서 유효한 사용인지 걸러준다는 가정으로 여긴 사용자 검증 X
    Member member1 = new Member();
    member1.setId(1L);

    // 뮤지컬이 유효한지 검사

    // 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket ticket = Ticket.builder()
        .member(member1)
        .ticketStatus(TicketStatus.BOOKED)
        .build();
    ticketRepository.save(ticket);
    return ticket;
  }

  @Override
  public Page<Ticket> getUserTickets(Long id, int page, int size) {
    id = 1L; // 이거 로그인 구현 전 임시 데이터..
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberId(id, pageable);
  }

  @Override
  public void checkTicket(long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  @Override
  public void deleteTicket(Member member, long ticketId) {

    // 로그인한 사용자와 취소할 티켓의 주인이 동일한지 검사
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    if(ticket.getMember().getId() != member.getId()) {
      throw new RuntimeException();
    }

    // soft delete
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }


}
