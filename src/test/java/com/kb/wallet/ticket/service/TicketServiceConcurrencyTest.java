package com.kb.wallet.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.domain.Section;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    AppConfig.class
})
@WebAppConfiguration
@Transactional
public class TicketServiceConcurrencyTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private SeatRepository seatRepository;

  @Autowired
  private TicketService ticketService;

  @Autowired
  private MusicalRepository musicalRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private EntityManager em;
  private static final int availableSeats = 2;

  private final String deviceId = "device123";

  private static final String[] EMAILS = {
      "user1@example.com",
      "user2@example.com",
      "user3@example.com",
      "user4@example.com",
      "user5@example.com",
      "user6@example.com",
      "user7@example.com",
      "user8@example.com",
      "user9@example.com",
      "user10@example.com"
  };

  static List<Member> members;
  static List<Seat> seats;
  private Musical testMusical;
  private Section testSection;
  private Schedule testSchedule;
  private boolean isInitialized = false;

  @BeforeEach
  public void setup() {
    if (!isInitialized) {
      createMembers();
      memberRepository.saveAll(members);
      testMusical = createTestMusical(musicalRepository);
      testSchedule = createTestSchedule(scheduleRepository, testMusical);
      createSeats(em);
      seatRepository.saveAll(seats);
      isInitialized = true;
    }
  }

  @Test
  public void testBookTicket_singleTicketSuccress() {
    Long seatId = 2L;
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(List.of(seatId));
    ticketRequest.setDeviceId(deviceId);

    List<TicketResponse> ticketResponses = ticketService.bookTicket(EMAILS[0], ticketRequest);

    assertThat(ticketResponses)
        .isNotNull()
        .isNotEmpty()
        .withFailMessage("티켓 응답이 비어 있지 않아야 합니다.")
        .hasSize(1)
        .describedAs("예약된 티켓 수가 1이어야 합니다.");

    TicketResponse ticketResponse = ticketResponses.get(0);
    assertNotNull(ticketResponse.getId(), "티켓 ID는 null이 아니어야 합니다.");
    //TODO: TicketResponse.seatId 추가
//    assertEquals(seatId, ticketResponse.getSeatId(), "좌석 ID가 일치해야 합니다.");
  }

  @Test
  @DisplayName("10명의 사용자가 동시에 티켓을 예매할 경우, 단 1건의 예몌만 성공한다.")
  public void testBookTicket_multipleUsersSungleSeatSuccress() throws InterruptedException {
    //given
    Long seatId = 1L;
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(List.of(seatId));
    ticketRequest.setDeviceId(deviceId);

    AtomicInteger successfulBookingsCount = new AtomicInteger(0);
    int createCnt = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch countDownLatch = new CountDownLatch(createCnt);

    for (String email : EMAILS) {
      executorService.submit(() -> {
        try {
          //when
          List<TicketResponse> responses = ticketService.bookTicket(email, ticketRequest);
          System.out.println("Booking responses for " + email + ": " + responses);
          if (responses.size() > 0) {
            successfulBookingsCount.incrementAndGet(); // 성공한 예매 카운트 증가
          }
        } finally {
          countDownLatch.countDown();

        }
      });
    }
    countDownLatch.await();
    executorService.shutdown();

    /**
     * 1. 이거 id가 뭔지를 모르겠는데 어떤 쿼리에서 조회되는 결과인지 아는 사람?
     * 뭐는 unread, 뭐는 null이고(이건 내가 값을 설정 안해줘서 그런 듯) 뭐는 id가 있어서 왜이런지 궁금
     * |---------|-------------|------------|--------|-----------|---|-----------|---------|-----------|-----------|---|-----------------|-----------------|----------|-------------|----------------|-----------------|--------|-------------|-------------------|---------------------|-------------|---|----------------|------|-----------|------|------------|-------|-----------------|-----------------|---------|-------------|----------------|-----------------|---------|-------------|-------------------|---------------------|---------|-------|---------|---------|-----------|-----------|
     * |id       |is_available |schedule_id |seat_no |section_id |id |date       |end_time |musical_id |start_time |id |detail_image_url |notice_image_url |place     |place_detail |place_image_url |poster_image_url |ranking |running_time |ticketing_end_date |ticketing_start_date |title        |id |available_seats |grade |musical_id |price |schedule_id |id     |detail_image_url |notice_image_url |place    |place_detail |place_image_url |poster_image_url |ranking  |running_time |ticketing_end_date |ticketing_start_date |title    |id     |date     |end_time |musical_id |start_time |
     * |---------|-------------|------------|--------|-----------|---|-----------|---------|-----------|-----------|---|-----------------|-----------------|----------|-------------|----------------|-----------------|--------|-------------|-------------------|---------------------|-------------|---|----------------|------|-----------|------|------------|-------|-----------------|-----------------|---------|-------------|----------------|-----------------|---------|-------------|-------------------|---------------------|---------|-------|---------|---------|-----------|-----------|
     * |[unread] |false        |1           |0       |1          |1  |2024-10-22 |null     |1          |20:00:00   |1  |null             |null             |Main Hall |1층           |null            |null             |1       |120          |2024-11-27         |2024-10-28           |test Musical |1  |0               |R     |[null]     |0     |[null]      |[null] |[unread]         |[unread]         |[unread] |[unread]     |[unread]        |[unread]         |[unread] |[unread]     |[unread]           |[unread]             |[unread] |[null] |[unread] |[unread] |[unread]   |[unread]   |
     * |---------|-------------|------------|--------|-----------|---|-----------|---------|-----------|-----------|---|-----------------|-----------------|----------|-------------|----------------|-----------------|--------|-------------|-------------------|---------------------|-------------|---|----------------|------|-----------|------|------------|-------|-----------------|-----------------|---------|-------------|----------------|-----------------|---------|-------------|-------------------|---------------------|---------|-------|---------|---------|-----------|-----------|
     *
     * 2. seatService.getSeatById 조회하면 seat0, schedule1, musical2, section3, musical4, schedule5
     * 이렇게 조회되는 거 같음 최적화가 된건지 궁금함
     */
    //then
    assertEquals(1, successfulBookingsCount.get(), "동시성 문제: 오직 한 명만 좌석 예약에 성공해야 함");
    //TODO: 첫번째 사용자 검증 필요
  }

  public static void createMembers() {
    members = new ArrayList<>();

    for (int i = 0; i < EMAILS.length; i++) {
      Member member = Member.builder()
          .email(EMAILS[i])
          .name("User" + (i + 1))
          .phone("0101234567" + i)
          .password("password")
          .pinNumber("1234")
          .role(RoleType.USER)
          .isActivated(true)
          .build();
      members.add(member);
    }
  }


  private static Musical createTestMusical(MusicalRepository musicalRepository) {
    return musicalRepository.save(Musical.builder()
        .title("test Musical")
        .ranking(1)
        .place("Main Hall")
        .placeDetail("1층")
        .ticketingStartDate(LocalDate.now())
        .ticketingEndDate(LocalDate.now().plusDays(30))
        .runningTime(120)
        .build());
  }

  private static Schedule createTestSchedule(ScheduleRepository scheduleRepository,
      Musical musical) {
    return scheduleRepository.save(Schedule.builder()
        .musical(musical)
        .date(LocalDate.of(2024, 10, 22))
        .startTime(LocalTime.of(20, 0))
        .build());
  }

  private void createSeats(EntityManager em) {
    testSection = Section.builder()
        .grade(Grade.R)
        .availableSeats(availableSeats)
        .build();
    em.persist(testSection);
    em.flush();
    seats = new ArrayList<>();

    for (int i = 1; i <= 2; i++) {
      Seat seat = Seat.builder()
          .id(Long.valueOf(i))
          .isAvailable(true)
          .schedule(testSchedule)
          .section(testSection)
          .build();
      seats.add(seat);
    }
  }
}