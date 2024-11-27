package com.kb.wallet.ticket.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketServiceTest {

  @InjectMocks
  private TicketServiceImpl ticketService;

  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private SeatService seatService;

  private Member member;
  private Musical musical;
  private Seat seat1;
  private Seat seat2;
  private Schedule schedule;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    member = new Member();
    member.setEmail("test@example.com");
    musical = mock(Musical.class);
    seat1 = mock(Seat.class);
    seat2 = mock(Seat.class);
    schedule = mock(Schedule.class);

    when(schedule.getDate()).thenReturn(LocalDate.of(2024, 11, 15));
    when(schedule.getStartTime()).thenReturn(LocalTime.of(19, 0));
    when(seat1.getSchedule()).thenReturn(schedule);
    when(seat2.getSchedule()).thenReturn(schedule);
    when(schedule.getMusical()).thenReturn(musical);
  }

  @Test
  @DisplayName("티켓 조회 성공")
  void getTicket_Success() {
    Ticket ticket = Ticket.builder()
        .id(1L)
        .member(member)
        .musical(musical)
        .ticketStatus(TicketStatus.BOOKED)
        .seat(seat1)
        .validUntil(LocalDateTime.now().plusDays(7))
        .cancelUntil(LocalDateTime.now().minusDays(7))
        .deviceId("device123")
        .build();

    // given
    when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

    // when
    Ticket result = ticketService.getTicket(1L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    verify(ticketRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("티켓 조회 실패 - 티켓 미발견")
  void getTicket_NotFound() {
    // given
    when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> ticketService.getTicket(1L));
    assertThat(exception.getMessage()).isEqualTo("티켓을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("티켓 예약 실패 - 좌석 이미 예약됨")
  void bookTicket_SeatAlreadyBooked() {
    TicketRequest request = new TicketRequest();
    request.setSeatId(Collections.singletonList(1L));
    request.setDeviceId("device123");

    // given
    when(memberService.getMemberByEmail("test@example.com")).thenReturn(member);
    when(seatService.getSeatById(1L)).thenReturn(seat1);
    doThrow(new CustomException(ErrorCode.TICKET_NOT_FOUND_ERROR)).when(seat1).checkSeatAvailability();

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> ticketService.bookTicket("test@example.com", request));
    assertThat(exception.getMessage()).isEqualTo("티켓을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("티켓 취소 성공")
  void cancelTicket_Success() {
    Long ticketId = 1L;

    Ticket ticket = Ticket.builder()
        .id(ticketId)
        .member(member)
        .ticketStatus(TicketStatus.BOOKED)
        .build();

    // given
    when(ticketRepository.findByTicketIdAndEmail(ticketId, member.getEmail())).thenReturn(Optional.of(ticket));

    // when
    ticketService.cancelTicket("test@example.com", ticketId);

    // then
    assertThat(ticket.getTicketStatus()).isEqualTo(TicketStatus.CANCELED);
    verify(ticketRepository, times(1)).save(ticket);
  }

  @Test
  @DisplayName("티켓 취소 실패 - 존재하지 않는 티켓")
  void cancelTicket_NotFound() {
    Long ticketId = 1L;

    // given
    when(ticketRepository.findByTicketIdAndEmail(ticketId, "test@example.com")).thenReturn(Optional.empty());

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> ticketService.cancelTicket("test@example.com", ticketId));
    assertThat(exception.getMessage()).isEqualTo("티켓을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("사용자 티켓 목록 조회 성공")
  void getTickets_Success() {
    // given
    TicketListResponse response = TicketListResponse.builder()
        .id(1L)
        .ticketStatus(TicketStatus.BOOKED)
        .build();
    List<TicketListResponse> responses = Collections.singletonList(response);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    when(ticketRepository.findAllByMemberAndTicketStatus("test@example.com", TicketStatus.BOOKED, null, pageable))
        .thenReturn(responses);

    // when
    List<TicketListResponse> result = ticketService.getTickets("test@example.com", TicketStatus.BOOKED, 0, 10, null);

    // then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getId()).isEqualTo(response.getId());
  }

  @Test
  @DisplayName("티켓 예약 성공")
  void bookTicket_Success() {
    TicketRequest request = new TicketRequest();
    request.setSeatId(Arrays.asList(1L, 2L));
    request.setDeviceId("device123");

    // given
    when(memberService.getMemberByEmail("test@example.com")).thenReturn(member);
    when(seatService.getSeatById(1L)).thenReturn(seat1);
    when(seatService.getSeatById(2L)).thenReturn(seat2);

    LocalDateTime now = LocalDateTime.now();
    Ticket ticket1 = Ticket.builder()
        .id(1L)
        .createdAt(now)
        .validUntil(now.plusHours(2))
        .cancelUntil(now.minusDays(7))
        .ticketStatus(TicketStatus.BOOKED)
        .deviceId("device123")
        .build();

    Ticket ticket2 = Ticket.builder()
        .id(2L)
        .createdAt(now)
        .validUntil(now.plusHours(2))
        .cancelUntil(now.minusDays(7))
        .ticketStatus(TicketStatus.BOOKED)
        .deviceId("device123")
        .build();

    when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket1, ticket2);

    // when
    List<TicketResponse> responses = ticketService.bookTicket("test@example.com", request);

    // then
    assertThat(responses).isNotNull();
    assertThat(responses.size()).isEqualTo(2);
    verify(seat1, times(1)).checkSeatAvailability();
    verify(seat2, times(1)).checkSeatAvailability();
    verify(seat1, times(1)).updateSeatAvailability();
    verify(seat2, times(1)).updateSeatAvailability();
    verify(schedule, times(2)).getMusical();
    verify(seatService, times(1)).getSeatById(1L);
    verify(seatService, times(1)).getSeatById(2L);
  }
}
