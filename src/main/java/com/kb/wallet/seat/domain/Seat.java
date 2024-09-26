package com.kb.wallet.seat.domain;

import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.domain.Ticket;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

  @ManyToOne
  @JoinColumn(name = "section_id")
  private Section section;

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;

  @Column
  private boolean isAvailable;

}
