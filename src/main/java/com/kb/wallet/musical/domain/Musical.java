package com.kb.wallet.musical.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "musical")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Musical {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String title;

  @Column(nullable = false)
  private int ranking;

  @Column(nullable = false, length = 30)
  private String place;

  @Column(nullable = false, length = 50)
  private String placeDetail;

  @Column(nullable = false)
  private LocalDate ticketingStartDate;

  @Column(nullable = false)
  private LocalDate ticketingEndDate;

  @Column(nullable = false)
  private int runningTime;

  private String posterImageUrl;

  private String noticeImageUrl;

  private String detailImageUrl;

  private String placeImageUrl;

}