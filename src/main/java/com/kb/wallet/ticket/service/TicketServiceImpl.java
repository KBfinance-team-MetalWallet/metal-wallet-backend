package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.domain.TicketExchange;
import com.kb.wallet.ticket.dto.request.CreateTicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.CreateTicketRequest;
import com.kb.wallet.ticket.dto.response.CreateTicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.CreateTicketResponse;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import com.kb.wallet.ticket.repository.TicketExchangeRepository;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.time.LocalDate;
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


  @Override
  public CreateTicketResponse saveTicket(Member member, CreateTicketRequest ticketRequest) {
    // 일정 테이블에서 일정 찾아서 넣어줘야 함.
    // TODO : 임의 member 생성.. 로그인 구현 시 삭제 해야 함
    Member temp = new Member();
    temp.setId(1L);

    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket bookedTicket = Ticket.createBookedTicket(ticketRequest);
    Ticket ticket = ticketRepository.save(bookedTicket);
    return CreateTicketResponse.toTicketResponse(ticket);
  }

  @Override
  public Page<CreateTicketResponse> findAllBookedTickets(Long id, int page, int size) {
    id = 1L; // TODO: 이거 로그인 구현 시 지워야 함
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Ticket> ticketsByMemberIdAndTicketStatus =
        ticketRepository.findTicketsByMemberIdAndTicketStatus(id, TicketStatus.BOOKED, pageable);
    return ticketsByMemberIdAndTicketStatus.map(CreateTicketResponse::toTicketResponse);
  }

  @Override
  public void checkTicket(long ticketId) {
    // TODO : GlobalException로 바꿔 주세요.
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);
  }

  public LocalDate convertStringToLocalDate(String dateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(dateStr, formatter);
  }

  @Override
  public CreateTicketExchangeResponse createTicketExchange(Member member,
      CreateTicketExchangeRequest exchangeRequest) {
    // 티켓 유효성 검사
    Ticket ticket = ticketRepository.findById(exchangeRequest.getTicketId())
        .orElseThrow(() -> new RuntimeException());

    if (ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new RuntimeException("INVALID_TICKET");
    }

    // 뮤지컬 상영 가능한 유효한 범위의 선택 날짜인지 비교
    LocalDate localDate = convertStringToLocalDate(exchangeRequest.getPreferredDate());
    Schedule schedule = scheduleRepository.findByStartTime(localDate)
        .orElseThrow(() -> new RuntimeException("NO_MATCHED_START_TIME"));

    String preferredDate = exchangeRequest.getPreferredDate();
    // 뮤지컬 상영 기간 내에 선택한 날짜인지 비교

    // 좌석 등급이 기존에 신청한 것과 동일한지 비교
//    if(ticket.g)

    checkOwner(member, ticket);

    TicketExchange ticketExchange = TicketExchange.toTicketExchange(ticket, exchangeRequest);

    TicketExchange saved = ticketExchangeRepository.save(ticketExchange);
    return CreateTicketExchangeResponse.createTicketExchangeResponse(saved);
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
        .orElseThrow(() -> new RuntimeException());

    checkTicketOwner(ticket, member);
    checkIfTicketIsBooked(ticket);

    // soft delete
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }

  private void checkTicketOwner(Ticket ticket, Member member) {
    if (ticket.getMember().getId() != member.getId()) {
      throw new RuntimeException();
    }
  }

  private void checkIfTicketIsBooked(Ticket ticket) {
    if (ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new RuntimeException();
    }
  }

}
