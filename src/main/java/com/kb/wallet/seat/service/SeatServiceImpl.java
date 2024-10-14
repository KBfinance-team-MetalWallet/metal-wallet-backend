package com.kb.wallet.seat.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

  private final SeatRepository seatRepository;

  @Override
  public Seat getSeatById(Long seatId) {
    return seatRepository.findById(seatId)
        .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND_ERROR));
  }

  @Override
  public void checkSeatAvailability(Seat seat) {
    if (!seat.isAvailable()) {
      throw new CustomException(ErrorCode.SEAT_ALREADY_BOOKED_ERROR);
    }
  }
}
