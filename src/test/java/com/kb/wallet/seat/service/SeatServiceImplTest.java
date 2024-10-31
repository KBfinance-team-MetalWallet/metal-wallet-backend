package com.kb.wallet.seat.service;

import static com.kb.wallet.global.common.status.ErrorCode.SEAT_NOT_FOUND_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.repository.SeatRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

  @InjectMocks
  private SeatServiceImpl seatService;

  @Mock
  private SeatRepository seatRepository;

  @Test
  @DisplayName("seatId로 좌석 조회 성공")
  void testGetSeatById_Success() {
    // given
    Long seatId = 1L;
    Seat seat = Seat.builder().id(seatId).build();

    given(seatRepository.findById(seatId)).willReturn(Optional.of(seat));

    // when
    Seat seatById = seatService.getSeatById(seatId);

    // then
    assertEquals(seat, seatById);
    assertEquals(seatId, seatById.getId());
  }

  @Test
  @DisplayName("존재하지 않는 좌석 조회")
  void testGetSeatById_Fail() {
    // given
    Long seatId = 2L;
    given(seatRepository.findById(seatId)).willReturn(Optional.empty());

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> seatService.getSeatById(seatId));

    // then
    assertEquals(SEAT_NOT_FOUND_ERROR, exception.getErrorCode());
  }

}