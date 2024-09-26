package com.kb.wallet.seat.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Section {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TODO : 뮤지컬 매핑 해줘야 함

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;

  @Column(nullable = false)
  private Grade grade;

  @ColumnDefault("0")
  private int price;
}
