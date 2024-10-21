package com.kb.wallet.ticket.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.ticket.constant.TicketStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ticketTest {

  private Member member;
  private Musical musical;
  private Seat seat;
  private Schedule schedule;

  @BeforeEach
  void setUp() {
    schedule = Schedule.builder()
      .date(LocalDate.now().plusDays(14))
      .startTime(LocalTime.of(19, 30))
      .build();

    seat = Seat.builder()
      .schedule(schedule)
      .build();

    member = Member.builder()
      .email("test@test.com")
      .build();

    musical = Musical.builder()
      .title("뮤지컬 테스트")
      .build();
  }

  @Test
  @DisplayName("예약된 티켓 생성 시 올바른 상태값 설정")
  void createBookedTicket_ShouldSetCorrectStatus() {
    // When
    Ticket ticket = Ticket.createBookedTicket(member, musical, seat);

    // Then
    assertEquals(TicketStatus.BOOKED, ticket.getTicketStatus());
    assertEquals(member, ticket.getMember());
    assertEquals(musical, ticket.getMusical());
    assertEquals(seat, ticket.getSeat());
  }

  @Test
  @DisplayName("예약된 티켓의 유효기간과 취소기한 설정 검증")
  void createBookedTicket_ShouldSetCorrectDates() {
    // When
    Ticket ticket = Ticket.createBookedTicket(member, musical, seat);
    LocalDateTime expectedValidUntil = LocalDateTime.of(schedule.getDate(),
      schedule.getStartTime());
    LocalDateTime expectedCancelUntil = expectedValidUntil.minusDays(7);

    // Then
    assertEquals(expectedValidUntil, ticket.getValidUntil());
    assertEquals(expectedCancelUntil, ticket.getCancelUntil());
  }

  @Test
  @DisplayName("티켓 취소 가능 여부 검증")
  void isCancellable_ShouldReturnCorrectValue() {
    // Given
    Ticket bookedTicket = createTicketWithStatus(TicketStatus.BOOKED);
    Ticket checkedTicket = createTicketWithStatus(TicketStatus.CHECKED);
    Ticket canceledTicket = createTicketWithStatus(TicketStatus.CANCELED);

    // Then
    assertTrue(bookedTicket.isCancellable());
    assertFalse(checkedTicket.isCancellable());
    assertFalse(canceledTicket.isCancellable());
  }

  @Test
  @DisplayName("티켓 교환 요청 상태 검증")
  void isExchangeRequested_ShouldReturnCorrectValue() {
    // Given
    Ticket exchangeRequestedTicket = createTicketWithStatus(TicketStatus.EXCHANGE_REQUESTED);
    Ticket bookedTicket = createTicketWithStatus(TicketStatus.BOOKED);

    // Then
    assertTrue(exchangeRequestedTicket.isExchangeRequested());
    assertFalse(bookedTicket.isExchangeRequested());
  }

  private Ticket createTicketWithStatus(TicketStatus status) {
    return Ticket.builder()
      .member(member)
      .musical(musical)
      .seat(seat)
      .ticketStatus(status)
      .build();
  }
}