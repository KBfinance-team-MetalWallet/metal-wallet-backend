package com.kb.wallet.seat.domain;

import static com.kb.wallet.global.common.status.ErrorCode.SEAT_ALREADY_BOOKED_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.ticket.domain.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatTest {

  private Seat seat;
  private Section section;

  @BeforeEach
  void setUp() {
    section = mock(Section.class);
    seat = Seat.builder()
        .id(1L).seatNo(1)
        .section(section)
        .schedule(mock(Schedule.class))
        .isAvailable(true)
        .build();
  }

  @Test
  @DisplayName("좌석 예약 가능 상태")
  void testCheckSeatAvailability_Success() {
    seat.checkSeatAvailability();
  }

  @Test
  @DisplayName("좌석 이미 예약된 상태")
  void testCheckSeatAvailability_AlreadyBookedFail() {
    // given
    seat.updateSeatAvailability();

    // when
    CustomException exception = assertThrows(CustomException.class, seat::checkSeatAvailability);

    // then
    assertEquals(SEAT_ALREADY_BOOKED_ERROR, exception.getErrorCode());
  }

  @Test
  @DisplayName("좌석 예약 시 seat, section 상태 변경 확인")
  void testUpdateSeatAvailability_CheckStatus() {
    // when
    seat.updateSeatAvailability();

    // then
    assertFalse(seat.isAvailable());
    verify(section).decrementAvailableSeats(); // verify: 실행 여부만 검증
  }
}