package com.kb.wallet.ticket.domain;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column
  private LocalDate date;

  @ManyToOne
  @JoinColumn(name = "musical_id", nullable = false)
  private Musical musical;

  @Column
  private LocalTime startTime;

  @Column
  private LocalTime endTime;
}
