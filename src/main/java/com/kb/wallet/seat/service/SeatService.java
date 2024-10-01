package com.kb.wallet.seat.service;

import com.kb.wallet.seat.domain.Seat;

public interface SeatService {

  Seat getSeatById(Long seatId);

  void checkSeatAvailability(Seat seat);
}
