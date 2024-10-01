package com.kb.wallet.ticket.domain;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    @Enumerated(value = EnumType.STRING)
    // enum의 값을 index가 아닌 텍스트 값 그대로 저장하고 싶을 때 위의 어노테이션 사용
    private TicketStatus ticketStatus;

    @OneToOne
    @JoinColumn(name = "seat_id")  // 외래 키 컬럼 지정
    private Seat seat;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime validUntil;

    @Column
    private LocalDateTime cancelUntil;

    // TODO : 변환 내용 완성해야 함
    public static Ticket createBookedTicket(Member member, TicketRequest ticketRequest) {
        return Ticket.builder()
            .member(member)
            .ticketStatus(TicketStatus.BOOKED)
            .build();
    }
}
