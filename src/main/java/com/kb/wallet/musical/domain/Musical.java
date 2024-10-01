package com.kb.wallet.musical.domain;

import java.time.LocalDate;
import javax.persistence.Column;
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
  private int ranking = 0;

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
  // Todo : Url(링크) 속성도 추후에 활용해야 함. 현재 CRUD에는 필요하지 않아서 사용X.
//    @Column
//    @NotNull
//    private String noticeImageUrl;
//    @Column
//    @NotNull
//    private String posterImageUrl;
//    @Column
//    @NotNull
//    private String detailImageUrl;
//    @Column
//    @NotNull
//    private String placeImageUrl;

}