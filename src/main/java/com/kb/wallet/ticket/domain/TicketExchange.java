package com.kb.wallet.ticket.domain;

import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Builder
public class TicketExchange {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;

  @Column
  private int preferredSeat;

  @Column
  private String preferredSchedule;

  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime updatedAt;

  public static TicketExchange toTicketExchange(Ticket ticket,
      TicketExchangeRequest exchangeRequest) {
    return TicketExchange.builder()
        .ticket(ticket)
        .preferredSeat(exchangeRequest.getPreferredSeatIndex())
        .preferredSchedule(exchangeRequest.getPreferredSchedule())
        .build();
  }
}
