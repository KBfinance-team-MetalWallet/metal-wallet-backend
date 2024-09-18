package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.time.LocalDateTime;
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
    // 일정 테이블에서 일정 찾아서 넣어줘야 함.
    // TODO : 임의 member 생성.. 로그인 구현 시 삭제 해야 함
    Member member1 = new Member();
    member1.setId(1L);

    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket ticket = Ticket.builder()
        .member(member1)
        .ticketStatus(TicketStatus.BOOKED)
        .build();
    ticketRepository.save(ticket);
    return ticket;
  }

  @Override
  public Page<Ticket> findAllUserTicket(Long id, int page, int size) {
    id = 1L; // TODO: 이거 로그인 구현 시 지워야 함
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberId(id, pageable);
  }

  @Override
  public void checkTicket(long ticketId) {
    // TODO : GlobalException로 바꿔 주세요.
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  @Override
  public void deleteTicket(Member member, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    checkTicketOwner(ticket, member);
    checkIfTicketIsBooked(ticket);

    // soft delete
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }

  private void checkTicketOwner(Ticket ticket, Member member) {
    if(ticket.getMember().getId() != member.getId()) {
      throw new RuntimeException();
    }
  }

  private void checkIfTicketIsBooked(Ticket ticket) {
    if(ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new RuntimeException();
    }
  }

}
