package com.kb.wallet.musical.dto.response;

import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.seat.domain.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicalScheduleSeatAvailabilityResponse {

  private Long seatId;
  private int seatNo;
  private Grade grade;

  public MusicalScheduleSeatAvailabilityResponse(Seat seat) {
    this.seatId = seat.getId();
    this.seatNo = seat.getSeatNo();
    this.grade = seat.getSection().getGrade();
  }
}


