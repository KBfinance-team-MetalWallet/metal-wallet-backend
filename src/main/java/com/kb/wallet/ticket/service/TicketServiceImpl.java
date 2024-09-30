package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.EXCHANGE_REQUESTED;

import com.kb.wallet.global.exception.CustomException;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;

import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.*;
import com.kb.wallet.ticket.dto.response.*;

import com.kb.wallet.ticket.repository.TicketExchangeRepository;

import com.kb.wallet.ticket.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketCheckService ticketCheckService;
  private final TicketRepository ticketRepository;
  private final TicketExchangeRepository ticketExchangeRepository;
  private final TicketMapper ticketMapper;
  private final MemberService memberService;




  @Override
  public TicketResponse saveTicket(Member member, TicketRequest ticketRequest) {
    Member memberFromDB = memberService.getMemberByEmail(member.getEmail());
    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket bookedTicket = Ticket.createBookedTicket(memberFromDB, ticketRequest);
    Ticket savedTicket = ticketRepository.save(bookedTicket);
    return TicketResponse.toTicketResponse(savedTicket);
  }

  @Override
  public Ticket findTicket(Long memberId, Long ticketId) {
    return ticketRepository.findByIdAndMemberId(memberId, ticketId).orElseThrow(() -> new RuntimeException("해당 id의 티켓이 없습니다."));
  }

  @Override
  public Page<TicketResponse> findAllBookedTickets(String email, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Ticket> ticketsByMemberIdAndTicketStatus =
        ticketRepository.findTicketsByMemberIdAndTicketStatus(email, TicketStatus.BOOKED, pageable);
    return ticketsByMemberIdAndTicketStatus.map(TicketResponse::toTicketResponse);
  }

  public void updateStatusChecked(Ticket ticket) {
    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  @Override
  public void deleteTicket(Member member, long ticketId) {
    Ticket ticket = findTicketById(ticketId);

    ticketCheckService.checkTicketOwner(ticket, member);
    ticketCheckService.checkIfTicketIsBooked(ticket);

    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
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
  public boolean isTicketAvailable(Long memberId, Ticket ticket) {
    memberService.findById(memberId);

    if(!ticket.getMember().getId().equals(memberId) ||
        !ticket.getTicketStatus().equals(TicketStatus.BOOKED)) {
      throw new CustomException(TICKET_STATUS_INVALID);
    }

    return true;
  }
}
