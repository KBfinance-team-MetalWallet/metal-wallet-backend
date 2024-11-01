package com.kb.wallet.seat.domain;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.domain.Schedule;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Section {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "musical_id")
  private Musical musical;

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Grade grade;

  @ColumnDefault("0")
  private int price;

  private int availableSeats;

  public void decrementAvailableSeats() {
    if (this.availableSeats <= 0) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_AVAILABLE_SEATS_ERROR);
    }
    this.availableSeats--;
  }
}
