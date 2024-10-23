package com.kb.wallet.ticket.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;


import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.ticket.constant.TicketStatus;
import java.time.*;
import org.junit.jupiter.api.*;

public class TicketDomainTest {

  private Member member;
  private Musical musical;
  private Seat seat;
  private Schedule schedule;
  private String deviceId;

  // 공통 데이터 초기화
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

    deviceId = "device123";
  }

  @Test
  @DisplayName("예약된 티켓 생성 시 올바른 상태값 설정")
  void createBookedTicket_ShouldSetCorrectStatus() {
    //Given - When
    Ticket ticket = Ticket.createBookedTicket(member, musical, seat, deviceId);

    // Then
    assertThat(ticket.getTicketStatus())
        .as("Ticket status should be BOOKED")
        .isEqualTo(TicketStatus.BOOKED);
    assertThat(ticket.getMember())
        .as("Ticket should be assigned to the correct member")
        .isEqualTo(member);
    assertThat(ticket.getMusical())
        .as("Ticket should be for the correct musical")
        .isEqualTo(musical);
    assertThat(ticket.getDeviceId())
        .as("Ticket should have the correct device ID")
        .isEqualTo(deviceId);}

  @Test
  @DisplayName("예약된 티켓의 유효기간과 취소기한 설정 검증")
  void createBookedTicket_ShouldSetCorrectDates() {
    // Given - When
    Ticket ticket = Ticket.createBookedTicket(member, musical, seat, deviceId);

    // Then
    LocalDateTime expectedValidUntil = LocalDateTime.of(schedule.getDate(),
        schedule.getStartTime());
    LocalDateTime expectedCancelUntil = expectedValidUntil.minusDays(7);

    assertThat(ticket.getValidUntil())
        .as("Valid until date should match")
        .isEqualTo(expectedValidUntil);
    assertThat(ticket.getCancelUntil())
        .as("Cancel until date should match")
        .isEqualTo(expectedCancelUntil);
  }

  @Test
  @DisplayName("티켓 취소 가능 여부 검증")
  void isCancellable_ShouldReturnCorrectValue() {
    // Given
    Ticket bookedTicket = createTicketWithStatus(TicketStatus.BOOKED);
    Ticket checkedTicket = createTicketWithStatus(TicketStatus.CHECKED);
    Ticket canceledTicket = createTicketWithStatus(TicketStatus.CANCELED);

    // When - Then
    assertThatCode(bookedTicket::isCancellable)
        .as("Booked Ticket should be cancellable and should not throw an exception")
        .doesNotThrowAnyException();

    Throwable checkedException = catchThrowable(checkedTicket::isCancellable);
    assertThat(checkedException)
        .as("Checked Ticket should not be cancellable and should throw CustomException")
        .isInstanceOf(CustomException.class)
        .hasMessage("티켓은 체크된 상태이므로 취소할 수 없습니다.");

    Throwable canceledException = catchThrowable(canceledTicket::isCancellable);
    assertThat(canceledException)
        .as("Canceled Ticket should not be cancellable and should throw CustomException")
        .isInstanceOf(CustomException.class)
        .hasMessage("티켓은 이미 취소된 상태입니다.");

  }
  @Test
  @DisplayName("잘못된 디바이스 ID 또는 BOOKED 상태가 아닌 티켓에 대해 validateCheckedChange 메서드가 예외를 발생시킴")
  void validateCheckedChange_ShouldThrowExceptionForInvalidDeviceOrStatus() {
    // Given
    Ticket bookedTicket = createTicketWithStatus(TicketStatus.BOOKED);
    Ticket checkedTicket = createTicketWithStatus(TicketStatus.CHECKED);

    // When - Then
    Throwable invalidDeviceException = catchThrowable(() -> bookedTicket.validateCheckedChange("invalidDevice123"));
    assertThat(invalidDeviceException)
        .as("Invalid device ID should throw CustomException")
        .isInstanceOf(CustomException.class)
        .hasMessage("디바이스 ID가 일치하지 않습니다.");

    Throwable invalidStatusException = catchThrowable(() -> checkedTicket.validateCheckedChange(deviceId));
    assertThat(invalidStatusException)
        .as("Ticket status not being BOOKED should throw CustomException")
        .isInstanceOf(CustomException.class)
        .hasMessage("티켓 상태가 BOOKED가 아닙니다.");
  }

  @Test
  @DisplayName("티켓 상태가 올바르게 업데이트됨")
  void updateTicketStatus_ShouldUpdateStatusCorrectly() {
    // Given
    Ticket ticket = createTicketWithStatus(TicketStatus.BOOKED);

    // When
    ticket.updateTicketStatus(TicketStatus.CHECKED);

    // Then
    assertThat(ticket.getTicketStatus())
        .as("Ticket status should be updated to CHECKED")
        .isEqualTo(TicketStatus.CHECKED);

    // 상태를 다른 상태로도 업데이트할 수 있는지 추가로 검증
    ticket.updateTicketStatus(TicketStatus.CANCELED);
    assertThat(ticket.getTicketStatus())
        .as("Ticket status should be updated to CANCELED")
        .isEqualTo(TicketStatus.CANCELED);
  }

  @Test
  @DisplayName("티켓이 BOOKED 상태가 아닐 때 예외가 발생")
  void isBooked_ShouldThrowExceptionIfNotBooked() {
    // Given
    Ticket bookedTicket = createTicketWithStatus(TicketStatus.BOOKED);
    Ticket checkedTicket = createTicketWithStatus(TicketStatus.CHECKED);

    // When - Then
    assertThatCode(bookedTicket::isBooked)
        .as("Ticket with BOOKED status should not throw an exception")
        .doesNotThrowAnyException();

    Throwable checkedException = catchThrowable(checkedTicket::isBooked);
    assertThat(checkedException)
        .as("Ticket with status other than BOOKED should throw CustomException")
        .isInstanceOf(CustomException.class)
        .hasMessage("예약 상태가 아닌 티켓입니다.");
  }


  private Ticket createTicketWithStatus(TicketStatus status) {
    LocalDateTime validUntil = LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
    return Ticket.builder()
        .member(member)
        .musical(musical)
        .seat(seat)
        .ticketStatus(status)
        .deviceId(deviceId)
        .validUntil(validUntil)
        .build();
  }
}

