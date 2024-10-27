package com.kb.wallet.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.domain.Section;
import com.kb.wallet.seat.service.SeatService;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
    AppConfig.class
})
@WebAppConfiguration
@Transactional
//@ActiveProfiles("test")
public class TicketServiceConcurrencyTest {

  @Mock
  private MemberService memberService;

  @Mock
  private SeatService seatService;

  @Mock
  private TicketRepository ticketRepository;

  @InjectMocks
  private TicketServiceImpl ticketService;

  private static final Long seatId = 1L;
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
  @Mock
  Seat seat;

  @Mock
  Section section;
  Musical musical;
  Schedule schedule;

  List<Member> members;

  @Mock
  Ticket testTicket;

  @BeforeEach
  public void setup() {

    MockitoAnnotations.openMocks(this);

    members = createMembers();
    for (int i = 0; i < members.size(); i++) {
      when(memberService.getMemberByEmail(members.get(i).getEmail())).thenReturn(members.get(i));
    }

    when(section.getId()).thenReturn(1L);
    when(section.getGrade()).thenReturn(Grade.R);
    when(section.getAvailableSeats()).thenReturn(1);

    Musical musical = mock(Musical.class);
    when(musical.getId()).thenReturn(1L);
    when(musical.getTitle()).thenReturn("test Musical");
    when(musical.getRanking()).thenReturn(1);
    when(musical.getPlace()).thenReturn("Main Hall");
    when(musical.getPlaceDetail()).thenReturn("1층");
    when(musical.getTicketingStartDate()).thenReturn(LocalDate.now());
    when(musical.getTicketingEndDate()).thenReturn(LocalDate.now().plusDays(30));
    when(musical.getRunningTime()).thenReturn(120);

    LocalDate mockDate = LocalDate.of(2024, 10, 22);
    LocalTime mockStartTime = LocalTime.of(20, 0);

    Schedule schedule = mock(Schedule.class);
    when(schedule.getId()).thenReturn(1L);
    when(schedule.getMusical()).thenReturn(musical);
    when(schedule.getDate()).thenReturn(mockDate);
    when(schedule.getStartTime()).thenReturn(mockStartTime);

    when(seat.getId()).thenReturn(seatId);
    when(seat.isAvailable()).thenReturn(true);
    when(seat.getSection()).thenReturn(section);
    /**
     * 헷갈리는 부분1,
     * mock을 써야할지, 생성자 호출을 해야할지
     * mock을 사용하니까 ticketService.bookTicketForSeat()의
     * Seat seat = seatService.getSeatById(seatId); 가 동작안함
     */
//    seat = createSeat();
    when(seatService.getSeatById(seatId)).thenReturn(seat);
  }

  @Test
  public void test1() {
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(List.of(seatId));
    ticketRequest.setDeviceId(deviceId);

    /**
     * 헷갈리는 부분2,
     * test()를 실행했을 때 setup()이 동작을 안해서 확인차 만들었음
     * seat.getId() NullPointerException 발생
     */
    ticketService.bookTicket(EMAILS[0], ticketRequest);


    verify(seatService).getSeatById(seatId);
  }

  @Test
  @DisplayName("10명의 사용자가 동시에 티켓을 예매할 경우, 단 1건의 예몌만 성공한다.")
  public void test() throws InterruptedException {
    //given
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(List.of(seatId));
    ticketRequest.setDeviceId(deviceId);

    /**
     * 헷갈리는 부분3,
     * save이외에 mock 처리 후 save를 확인하려고 했는데
     * 어디까지 mock처리를 해야할지 모르겠음
     * mock 처리 범위
     * ticketService에서 호출하는
     * 도메인의 비즈니스 로직, 생성자 호출, 다른 Service 또는 Repository까지라고 생각했는데
     * seat.checkSeatAvailability(); 와 같은 void 처리는 어떻게 mock 처리하는지?
     * Ticket.createBookedTicket() 생성자도 다른 도메인과 엮여있는지 어떻게 처리할지?
     */
    //mocking
    //musical, schedule, section, seat
    //ticketRepository.save
    //ticketResponse
    when(ticketRepository.save(any(Ticket.class)))
        .thenAnswer(invocation -> {
          Ticket ticket = invocation.getArgument(0);
          /**
           * syncronized 처리가 필요할 것으로 보임
           * test할 때 스레드가 순차적으로 처리되지 않음
           */
          return ticket;
        });

//    seat.checkSeatAvailability();

//    Ticket.createBookedTicket mockTicket 생성 후
//    ticketRepository.save mock 반환
//    seat.updateSeatAvailability 통과 or section 생성

    int createCnt = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch countDownLatch = new CountDownLatch(createCnt);

    List<TicketResponse> successfulBookings = Collections.synchronizedList(new ArrayList<>());

    for (String email : EMAILS) {
      executorService.submit(() -> {
        try {
          //when
          List<TicketResponse> responses = ticketService.bookTicket(email, ticketRequest);
          System.out.println("Booking responses for " + email + ": " + responses);
          if (!responses.isEmpty()) {
            successfulBookings.addAll(responses);
          }
        } finally {
          countDownLatch.countDown();

        }
      });
    }
    countDownLatch.await();

    //then
    verify(ticketRepository, atMostOnce()).save(any(Ticket.class));
    assertEquals(1, successfulBookings.size(), "동시성 문제: 오직 한 명만 좌석 예약에 성공해야 함");

    /**
     * 헷갈리는 부분4,
     * 위랑 아래 중에 어떻게 처리해야할지 모르겠음
     */
//    verify(ticketRepository, times(1)).save(any(Ticket.class));
    /**
     * Autowired 처리할 때 사용
     */
//    List<Ticket> bookedTickets = ticketRepository.findAll();
//    assertEquals(1, bookedTickets.size(), "동시성 문제: 생성된 티켓이 1개인지 확인");
  }

  /**
   * Autowired 처리할 때 사용
   */
  public static List<Member> createMembers() {
    List<Member> members = new ArrayList<>();

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

    return members;
  }

  public static Seat createSeat() {
    return Seat.builder()
        .id(seatId)
        .isAvailable(true)
        .build();
  }
}