package com.kb.wallet.seat.domain;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.ticket.domain.Schedule;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int seatNo;

  @ManyToOne
  @JoinColumn(name = "section_id")
  private Section section;

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;

  @Column
  private boolean isAvailable;
  public void checkSeatAvailability() {
    if (!this.isAvailable()) {
      throw new CustomException(ErrorCode.SEAT_ALREADY_BOOKED_ERROR);
    }
  }

  public void updateSeatAvailability() {
    this.isAvailable = false;
    this.section.decrementAvailableSeats();
  }

}
