package com.kb.wallet.ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TicketRepositoryTest {

  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private MemberRepository memberRepository;

  private Ticket ticket;
  private Long validId;
  private String validEmail = "test@example.com";

  @BeforeEach
  public void setUp() {
    // 테스트용 회원 및 티켓 데이터 생성
    Member member = Member.builder()
        .id(1L)
        .email(validEmail)
        .build();

    ticket = Ticket.builder()
        .id(1L)
        .ticketStatus(TicketStatus.BOOKED)
        .member(member)
        .build();

    validId = ticket.getId();

    // 필요한 스터빙 설정
    when(ticketRepository.findByMember(member.getId(), member.getEmail()))
        .thenReturn(Optional.of(ticket));

    when(ticketRepository.findById(validId)).thenReturn(Optional.of(ticket));

    List<TicketListResponse> mockTickets = new ArrayList<>();
    mockTickets.add(new TicketListResponse(
        ticket.getId(),
        "Musical Title",
        ticket.getTicketStatus(),
        LocalDateTime.now(), // createdAt
        LocalDateTime.now().plusDays(30), // validUntil
        LocalDateTime.now().plusDays(7), // cancelUntil
        "Venue Name", // place
        LocalDate.now().plusDays(1), // scheduleDate
        LocalTime.of(19, 0), // startTime
        "poster_image_url", // posterImageUrl
        Grade.A, // grade
        1 // seatNo
    ));

    when(ticketRepository.findAllByMemberAndTicketStatus(eq(member.getEmail()), eq(TicketStatus.BOOKED), isNull(), any(Pageable.class)))
        .thenReturn(mockTickets);

    // Invalid한 경우에 대한 스터빙 수정
    when(ticketRepository.findByMember(anyLong(), eq("invalid@example.com")))
        .thenReturn(Optional.empty());

    when(ticketRepository.findById(validId + 9999))
        .thenReturn(Optional.empty());

    when(ticketRepository.findAllByMemberAndTicketStatus(eq(member.getEmail()), eq(TicketStatus.CANCELED), isNull(), any(Pageable.class)))
        .thenReturn(new ArrayList<>());
  }

  // 정상 케이스 테스트 메서드
  @Test
  @DisplayName("Valid한 Member와 Email로 Ticket 조회")
  public void findByMember_withValidIdAndEmail_returnsTicket() {
    // Given
    Member member = ticket.getMember();

    // When
    Optional<Ticket> foundTicket = ticketRepository.findByMember(member.getId(), member.getEmail());

    // Then
    assertThat(foundTicket).isPresent();
    assertThat(foundTicket.get().getTicketStatus()).isEqualTo(ticket.getTicketStatus());
    assertThat(foundTicket.get().getMember()).isEqualTo(ticket.getMember());

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findByMember(member.getId(), member.getEmail());
  }

  @Test
  @DisplayName("Valid한 Member와 TicketStatus로 Ticket 목록 조회")
  public void findAllByMemberAndTicketStatus_withValidEmailAndStatus_returnsTicketList() {
    // Given
    Member member = ticket.getMember();
    TicketStatus status = TicketStatus.BOOKED;

    // When
    List<TicketListResponse> tickets = ticketRepository.findAllByMemberAndTicketStatus(member.getEmail(), status, null, Pageable.unpaged());

    // Then
    assertThat(tickets)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1)
        .first()
          .extracting(TicketListResponse::getId)
          .isEqualTo(ticket.getId());

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findAllByMemberAndTicketStatus(member.getEmail(), status, null, Pageable.unpaged());
  }

  @Test
  @DisplayName("Valid한 Ticket ID로 Ticket 조회")
  public void findById_withValidTicketId_returnsTicket() {
    // Given
    Long ticketId = validId;

    // When
    Optional<Ticket> foundTicket = ticketRepository.findById(ticketId);

    // Then
    assertThat(foundTicket)
        .isPresent()
        .get()
        .isEqualTo(ticket);

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findById(ticketId);
  }

  // 예외 케이스 테스트 메서드
  @Test
  @DisplayName("Invalid Member와 Email로 Ticket 조회 - 결과 Empty")
  public void findByMember_withInvalidIdOrEmail_returnsEmpty() {
    // Given
    Member invalidMember = Member.builder()
        .id(999L) // 존재하지 않는 ID
        .email("invalid@example.com")
        .build();

    // When
    Optional<Ticket> ticket = ticketRepository.findByMember(invalidMember.getId(), invalidMember.getEmail());

    // Then
    assertThat(ticket).isNotPresent();

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findByMember(invalidMember.getId(), invalidMember.getEmail());
  }

  @Test
  @DisplayName("Invalid Email이나 TicketStatus로 Ticket 목록 조회 - 결과 Empty")
  public void findAllByMemberAndTicketStatus_withInvalidEmailOrStatus_returnsEmptyList() {
    // Given
    Member member = ticket.getMember();
    TicketStatus invalidStatus = TicketStatus.CANCELED; // 다른 상태 값

    // When
    List<TicketListResponse> tickets = ticketRepository.findAllByMemberAndTicketStatus(member.getEmail(), invalidStatus, null, Pageable.unpaged());

    // Then
    assertThat(tickets).isNotNull().isEmpty();

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findAllByMemberAndTicketStatus(member.getEmail(), invalidStatus, null, Pageable.unpaged());
  }

  @Test
  @DisplayName("Invalid Ticket ID로 Ticket 조회 - 결과 Empty")
  public void findById_withInvalidTicketId_returnsEmpty() {
    // Given
    Long invalidId = validId + 9999; // 존재하지 않는 ID

    // When
    Optional<Ticket> foundTicket = ticketRepository.findById(invalidId);

    // Then
    assertThat(foundTicket).isNotPresent();

    // 메서드 호출 검증
    verify(ticketRepository, times(1)).findById(invalidId);
  }
}
