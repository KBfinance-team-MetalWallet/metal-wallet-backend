package com.kb.wallet.musical.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "musical")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Musical {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String title;

  @NotNull
  private int ranking;

  @NotNull
  private String place;

  @NotNull
  private String placeDetail;

  @NotNull
  private LocalDate ticketingStartDate;

  @NotNull
  private LocalDate ticketingEndDate;

  @NotNull
  private int runningTime;

  private String posterImageUrl;

  private String noticeImageUrl;

  private String detailImageUrl;

  private String placeImageUrl;

}