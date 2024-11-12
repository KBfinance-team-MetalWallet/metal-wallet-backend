package com.kb.wallet.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//테스트 인스턴스를 클래스 수준에서 관리하도록 하고, 각 테스트 메소드 간에 데이터가 유지되도록
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

  private static final String[] EMAILS = new String[11];

  static {
    for (int i = 0; i < EMAILS.length; i++) {
      EMAILS[i] = "user" + (i + 1) + "@example.com";
    }
  }

  static List<Member> members;
  static List<Seat> seats;
  private Musical testMusical;
  private Section testSection;
  private Schedule testSchedule;
  private boolean isInitialized = false;

  @Test
  @DisplayName("단건 티켓을 예매할 경우, 예몌가 성공한다.")
  public void testBookTicket_singleTicketSuccress() {
    Long seatId = 2L;
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(List.of(seatId));
    ticketRequest.setDeviceId(deviceId);

    Member member = Member.builder()
      .email(EMAILS[0])
      .name("User" + (0 + 1))
      .phone("010123456" + 0)
      .password("password")
      .pinNumber("1234")
      .role(RoleType.USER)
      .isActivated(true)
      .build();
    memberRepository.save(member);

    testMusical = createTestMusical(musicalRepository);
    testSchedule = createTestSchedule(scheduleRepository, testMusical);
    testSection = Section.builder()
      .grade(Grade.R)
      .availableSeats(availableSeats)
      .build();
    em.persist(testSection);
    em.flush();

    Seat seat = Seat.builder()
      .isAvailable(true)
      .schedule(testSchedule)
      .section(testSection)
      .build();
    seatRepository.save(seat);

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

    int startNo = 1;
    members = new ArrayList<>();
    for (int i = startNo; i < EMAILS.length; i++) {
      Member member = Member.builder()
        .email(EMAILS[i])
        .name("User" + (i + 1))
        .phone("010123456" + i)
        .password("password")
        .pinNumber("1234")
        .role(RoleType.USER)
        .isActivated(true)
        .build();
      members.add(member);
    }
    memberRepository.saveAll(members);
    if (memberRepository.findByEmail(EMAILS[1]).isPresent()) {
      System.out.println("email: 1");
    } else {
      System.out.println("email: 2");
    }

    testMusical = createTestMusical(musicalRepository);
    testSchedule = createTestSchedule(scheduleRepository, testMusical);
    testSection = Section.builder()
      .grade(Grade.R)
      .availableSeats(availableSeats)
      .musical(testMusical)
      .build();
    em.persist(testSection);
    em.flush();
    Seat seat = Seat.builder()
      .isAvailable(true)
      .schedule(testSchedule)
      .section(testSection)
      .build();
    seatRepository.save(seat);

    Long savedSeatId = seat.getId();
    System.out.println("savedSeatId: " + savedSeatId);

    if (seatRepository.findById(seatId).isPresent()) {
      System.out.println("seat ok: 2");
    }

    AtomicInteger successfulBookingsCount = new AtomicInteger(0);
    int createCnt = EMAILS.length - 1;
    ExecutorService executorService = Executors.newFixedThreadPool(createCnt);
    CountDownLatch countDownLatch = new CountDownLatch(createCnt);

    for (int i = startNo; i < EMAILS.length; i++) {
      String currentEmail = EMAILS[i];
      executorService.submit(() -> {
        try {
          if (memberRepository.findByEmail(currentEmail).isPresent()) {
            System.out.println("email: 3");
          } else {
            System.out.println("email: 4");
          }
          System.out.println(currentEmail);
          //when
          List<TicketResponse> responses = ticketService.bookTicket(currentEmail, ticketRequest);
          System.out.println("Booking responses for " + currentEmail + ": " + responses);
          if (responses.size() > 0) {
            successfulBookingsCount.incrementAndGet(); // 성공한 예매 카운트 증가
          }
        } catch (Exception e) {
          System.out.println(
            "Exception during booking for " + currentEmail + ": " + e.getMessage());
          e.printStackTrace();
        } finally {
          countDownLatch.countDown();

        }
      });
    }
    countDownLatch.await();
    executorService.shutdown();

    //then
    assertEquals(1, successfulBookingsCount.get(), "동시성 문제: 오직 한 명만 좌석 예약에 성공해야 함");
    //TODO: 첫번째 사용자 검증 필요
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
}
