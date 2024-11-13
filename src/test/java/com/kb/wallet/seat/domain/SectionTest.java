package com.kb.wallet.seat.domain;

import static com.kb.wallet.global.common.status.ErrorCode.NOT_ENOUGH_AVAILABLE_SEATS_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kb.wallet.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectionTest {

  private Section section;

  @BeforeEach
  void setUp() {
    section = Section.builder()
        .id(1L)
        .availableSeats(5)
        .build();
  }

  @Test
  @DisplayName("availableSeats 가 1 이상을 경우 성공")
  void testDecrementAvailableSeats_MoreThanZeroSuccess() {
    // given
    int originalSeatCnt = section.getAvailableSeats();

    // when
    section.decrementAvailableSeats();

    // then
    assertEquals(originalSeatCnt - 1, section.getAvailableSeats());
  }

  @Test
  @DisplayName("availableSeats 가 0 이하일 경우 예외 처리")
  void testDecrementAvailableSeats_LessThanOneFail() {
    // given
    section.setAvailableSeats(0);

    // when
    CustomException exception = assertThrows(CustomException.class,
        section::decrementAvailableSeats);

    // then
    assertEquals(NOT_ENOUGH_AVAILABLE_SEATS_ERROR, exception.getErrorCode());
  }
}