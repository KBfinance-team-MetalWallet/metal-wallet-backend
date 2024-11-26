package com.kb.wallet.ticket.repository;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@WebAppConfiguration
@Transactional
public class TicketRepositoryTest {

  @Autowired
  private TicketRepository ticketRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MusicalRepository musicalRepository;

  @Autowired
  private SeatRepository seatRepository;

  private Ticket ticket;
  private Member member;

  @BeforeEach
  public void setUp() {
    member = Member.builder()
        .email("test@example.com")
        .name("Test User")
        .password("password123")
        .phone("01012345678")
        .pinNumber("123456")
        .role(RoleType.USER)
        .isActivated(true)
        .build();

    member = memberRepository.save(member);

    Musical musical = Musical.builder()
        .title("Test Musical")
        .ranking(1)
        .place("Test Place")
        .placeDetail("Test Detail")
        .ticketingStartDate(LocalDate.now())
        .ticketingEndDate(LocalDate.now().plusDays(30))
        .runningTime(120)
        .build();
    musical = musicalRepository.save(musical);

    Seat seat = Seat.builder()
        .seatNo(1)
        .isAvailable(true)
        .build();
    seat = seatRepository.save(seat);

    ticket = Ticket.builder()
        .ticketStatus(TicketStatus.BOOKED)
        .member(member)
        .musical(musical)
        .seat(seat)
        .createdAt(LocalDateTime.now())
        .validUntil(LocalDateTime.now().plusDays(30))
        .cancelUntil(LocalDateTime.now().plusDays(7))
        .deviceId("device123")
        .build();

    ticket = ticketRepository.save(ticket);
  }

  @Test
  @DisplayName("Valid한 Member와 Email로 Ticket 조회")
  public void findByMember_withValidIdAndEmail_returnsTicket() {
    // Given
    Long memberId = ticket.getMember().getId();
    String email = ticket.getMember().getEmail();

    // When
    Optional<Ticket> foundTicket = ticketRepository.findByTicketIdAndEmail(memberId, email);

    // Then
    assertThat(foundTicket)
        .isPresent()
        .get()
        .isEqualTo(ticket);

  }


  @Test
  @DisplayName("Invalid Member와 Email로 Ticket 조회 - 결과 Empty")
  public void findByMember_withInvalidIdOrEmail_returnsEmpty() {
    // Given
    Long invalidId = 999L;
    String invalidEmail = "invalid@example.com";

    // When
    Optional<Ticket> ticket = ticketRepository.findByTicketIdAndEmail(invalidId, invalidEmail);

    // Then
    assertThat(ticket).isNotPresent();
  }

  @Test
  @DisplayName("Invalid Email이나 TicketStatus로 Ticket 목록 조회 - 결과 Empty")
  public void findAllByMemberAndTicketStatus_withInvalidEmailOrStatus_returnsEmptyList() {
    // Given
    String email = member.getEmail();
    TicketStatus invalidStatus = TicketStatus.CANCELED;
    Pageable pageable = PageRequest.of(0, 10);

    // When
    List<TicketListResponse> tickets = ticketRepository.findAllByMemberAndTicketStatus(email,
        invalidStatus, null, pageable);

    // Then
    assertThat(tickets).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Invalid Ticket ID로 Ticket 조회 - 결과 Empty")
  public void findById_withInvalidTicketId_returnsEmpty() {
    // Given
    Long invalidId = ticket.getId() + 9999;

    // When
    Optional<Ticket> foundTicket = ticketRepository.findById(invalidId);

    // Then
    assertThat(foundTicket).isNotPresent();

  }
}
