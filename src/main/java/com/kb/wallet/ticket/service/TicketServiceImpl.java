package com.kb.wallet.ticket.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import com.kb.wallet.ticket.exception.TicketException;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    Member temp = new Member();
    temp.setId(1L);

    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket ticket = Ticket.builder()
        .member(temp)
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
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND_ERROR, "티켓을 찾을 수 없습니다."));

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  @Override
  public void deleteTicket(Member member, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND_ERROR, "티켓을 찾을 수 없습니다."));

    checkTicketOwner(ticket, member);
    checkIfTicketIsBooked(ticket);

    // soft delete
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }

  private void checkTicketOwner(Ticket ticket, Member member) {
    if(ticket.getMember().getId() != member.getId()) {
      throw new TicketException(ErrorCode.FORBIDDEN_ERROR, "해당 티켓의 소유자가 아닙니다.");
    }
  }

  private void checkIfTicketIsBooked(Ticket ticket) {
    if(ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new TicketException(ErrorCode.FORBIDDEN_ERROR, "해당 티켓의 소유자가 아닙니다.");
    }
  }

}
