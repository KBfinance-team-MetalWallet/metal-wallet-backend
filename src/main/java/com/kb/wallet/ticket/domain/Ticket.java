package com.kb.wallet.ticket.domain;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.ticket.constant.TicketStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "musical_id")
  private Musical musical;

  @Column
  @Enumerated(value = EnumType.STRING)
  // enum의 값을 index가 아닌 텍스트 값 그대로 저장하고 싶을 때 위의 어노테이션 사용
  private TicketStatus ticketStatus;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id")  // 외래 키 컬럼 지정
  private Seat seat;

  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime validUntil;

  @Column
  private LocalDateTime cancelUntil;

  @Column
  private String deviceId;

  public static Ticket createBookedTicket(Member member, Musical musical, Seat seat) {

    LocalDateTime musicalStartDateTime = LocalDateTime.of(seat.getSchedule().getDate(),
      seat.getSchedule().getStartTime());
    LocalDateTime cancelUntilDateTime = musicalStartDateTime.minusDays(7); // 공연 시작 7일 전

    return Ticket.builder()
      .member(member)
      .musical(musical)
      .ticketStatus(TicketStatus.BOOKED)
      .seat(seat)
      .validUntil(musicalStartDateTime)
      .cancelUntil(cancelUntilDateTime)
      .deviceId(builder().deviceId)
      .build();
  }

  public boolean isCancellable() {
    return this.ticketStatus != TicketStatus.CANCELED && this.ticketStatus != TicketStatus.CHECKED;
  }

  public boolean isExchangeRequested() {
    return this.ticketStatus == TicketStatus.EXCHANGE_REQUESTED;
  }
}
