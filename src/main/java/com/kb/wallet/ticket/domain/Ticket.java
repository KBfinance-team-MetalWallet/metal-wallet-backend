package com.kb.wallet.ticket.domain;

import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;
import static com.kb.wallet.ticket.constant.TicketStatus.BOOKED;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
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
import lombok.AccessLevel;
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
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자를 사용하도록 강제
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
  private TicketStatus ticketStatus;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", unique = true)
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

  public static Ticket createBookedTicket(Member member, Musical musical, Seat seat, String deviceId) {

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
        .deviceId(deviceId)
        .build();
  }

  public void validateCheckedChange(String extractedDeviceId) {
    if (this.ticketStatus.equals(TicketStatus.BOOKED) || !this.deviceId
        .equals(extractedDeviceId)) {
      throw new CustomException(ErrorCode.TICKET_STATUS_INVALID);
    }
  }

  public void updateTicketStatus(TicketStatus status) {
    this.ticketStatus = status;
  }

  public void isCancellable() {
    if (this.ticketStatus == TicketStatus.CANCELED || this.ticketStatus == TicketStatus.CHECKED) {
      throw new CustomException(TICKET_STATUS_INVALID);
    }
  }

  public void isBooked() {
    if(this.ticketStatus != BOOKED) {
      throw new CustomException(TICKET_STATUS_INVALID, "예약 상태가 아닌 티켓입니다.");
    }
  }
}
