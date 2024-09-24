package com.kb.wallet.ticket.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.exception.TicketException;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepository;
  private final TicketExchangeRepository ticketExchangeRepository;
  private final ScheduleRepository scheduleRepository;
  private final TicketMapper ticketMapper;

  private final int GRADE_DIVISOR = 10;


  @Override
  public TicketResponse saveTicket(Member member, TicketRequest ticketRequest) {
    // 일정 테이블에서 일정 찾아서 넣어줘야 함.
    // TODO : 임의 member 생성.. 로그인 구현 시 삭제 해야 함
    Member temp = new Member();
    temp.setId(1L);

    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket bookedTicket = Ticket.createBookedTicket(ticketRequest);
    Ticket ticket = ticketRepository.save(bookedTicket);
    return TicketResponse.toTicketResponse(ticket);
  }

  @Override
  public Page<TicketResponse> findAllBookedTickets(Long id, int page, int size) {
    id = 1L; // TODO: 이거 로그인 구현 시 지워야 함
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Ticket> ticketsByMemberIdAndTicketStatus =
        ticketRepository.findTicketsByMemberIdAndTicketStatus(id, TicketStatus.BOOKED, pageable);
    return ticketsByMemberIdAndTicketStatus.map(TicketResponse::toTicketResponse);
  }

  @Override
  public void checkTicket(long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND_ERROR, "티켓을 찾을 수 없습니다."));

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  public LocalTime convertStringToLocalTime(String scheduleStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return LocalTime.parse(scheduleStr, formatter);
  }

  @Override
  public TicketExchangeResponse createTicketExchange(Member member,
      TicketExchangeRequest exchangeRequest) {

    Ticket ticket = checkTicketValidation(exchangeRequest);

    compareWithMusicalDate(exchangeRequest);

    compareWithOriginalSeatGrade(ticket, exchangeRequest);


    // TODO : 티켓 교환 알고리즘 작성해야 함 .

    // TODO : 로그인 이전엔 여기 null이라 에러뜹니다.
    checkOwner(member, ticket);

    TicketExchange ticketExchange = TicketExchange.toTicketExchange(ticket, exchangeRequest);
    TicketExchange saved = ticketExchangeRepository.save(ticketExchange);
    return TicketExchangeResponse.createTicketExchangeResponse(saved);
  }

  @Override
  public Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page,
      int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<TicketExchange> ticketExchanges = ticketExchangeRepository.findByTicketMember(member,
        pageable);
    return ticketExchanges.map(TicketExchangeResponse::createTicketExchangeResponse);
  }

  private Ticket checkTicketValidation(TicketExchangeRequest exchangeRequest) {
    Ticket ticket = ticketRepository.findById(exchangeRequest.getTicketId())
        .orElseThrow(() -> new RuntimeException());

    if (ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new RuntimeException("INVALID_TICKET");
    }
    return ticket;
  }

  private void compareWithMusicalDate(TicketExchangeRequest exchangeRequest) {
    LocalTime localTime = convertStringToLocalTime(exchangeRequest.getPreferredSchedule());
    if (!scheduleRepository.existsByStartTime(localTime)) {
      throw new RuntimeException("NO_MATCHED_START_TIME");
    }
  }

  private void compareWithOriginalSeatGrade(Ticket ticket, TicketExchangeRequest exchangeRequest) {
    int preferredGrade = exchangeRequest.getPreferredSeatIndex() / GRADE_DIVISOR;
    if (ticket.getSeat().getSection().getGrade() != Grade.fromValue(preferredGrade)) {
      throw new RuntimeException("SECTION_NOT_MATCH");
    }
  }

  private void checkOwner(Member member, Ticket ticket) {
    if (ticket.getMember() != member) {
      // TODO : ticket error TICKET_OWNER_MISMATCH
      throw new RuntimeException();
    }
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
