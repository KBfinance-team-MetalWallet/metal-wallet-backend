package com.kb.wallet.ticket.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
  AppConfig.class
})
@WebAppConfiguration
class TicketServiceTest {

  @Autowired
  private TicketService ticketService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  public void setUp() {
    // member 데이터 삽입
    jdbcTemplate.execute(
      "insert into member (id, email, is_activated, name, password, phone, pin_number, role) values "
        +
        "(1, 'test1@gmail.com', true, 'test1', 'password1', '01011111111', '111111', 'USER')");

    jdbcTemplate.execute(
      "insert into member (id, email, is_activated, name, password, phone, pin_number, role) values "
        +
        "(2, 'test2@gmail.com', true, 'test2', 'password2', '01022222222', '222222', 'USER')");

    // musical 데이터 삽입
    jdbcTemplate.execute(
      "insert into musical (id, place, place_detail, ranking, running_time, ticketing_end_date, ticketing_start_date, title, detail_image_url, notice_image_url, place_image_url, poster_image_url) values "
        +
        "(1, '서울', '서울 아트센터', 1, 150, '2024-10-16', '2024-10-01', '킹키부츠', null, null, null, null)");

    // schedule 데이터 삽입
    jdbcTemplate.execute(
      "insert into schedule (id, date, start_time, end_time, musical_id) values " +
        "(1, '2024-10-17', '10:00:00', '12:30:00', 1)");

    // section 데이터 삽입
    jdbcTemplate.execute(
      "insert into section (id, available_seats, grade, price, musical_id, schedule_id) values " +
        "(1, 100, 'R', 190000, 1, 1)");

    // seat 데이터 삽입
    jdbcTemplate.execute(
      "insert into seat (id, is_available, seat_no, schedule_id, section_id) values " +
        "(1, true, 1, 1, 1)");
  }

  @AfterEach
  void tearDown() {
    cleanUpAll();
  }

  private void cleanUpAll() {
    jdbcTemplate.execute("delete from ticket");
    jdbcTemplate.execute("delete from seat");
    jdbcTemplate.execute("delete from section");
    jdbcTemplate.execute("delete from schedule");
    jdbcTemplate.execute("delete from musical");
    jdbcTemplate.execute("delete from member");
  }

  @Test
  @DisplayName("모든 테이블에 데이터가 성공적으로 삽입된다.")
  void testDataInserted() {
    // Verify member data
    Long memberCount = jdbcTemplate.queryForObject(
      "select count(*) from member where email in ('test1@gmail.com', 'test2@gmail.com')",
      Long.class
    );
    assertEquals(2, memberCount, "Both members should be inserted successfully.");

    // Verify musical data
    Long musicalCount = jdbcTemplate.queryForObject(
      "select count(*) from musical where title = '킹키부츠' and place = '서울'",
      Long.class
    );
    assertEquals(1, musicalCount, "Musical should be inserted successfully.");

    // Verify schedule data
    Long scheduleCount = jdbcTemplate.queryForObject(
      "select count(*) from schedule where date = '2024-10-17' and musical_id = 1",
      Long.class
    );
    assertEquals(1, scheduleCount, "Schedule should be inserted successfully.");

    // Verify section data
    Long sectionCount = jdbcTemplate.queryForObject(
      "select count(*) from section where grade = 'R' and price = 190000",
      Long.class
    );
    assertEquals(1, sectionCount, "Section should be inserted successfully.");

    // Verify seat data
    Long seatCount = jdbcTemplate.queryForObject(
      "select count(*) from seat where seat_no = 1 and is_available = true",
      Long.class
    );
    assertEquals(1, seatCount, "Seat should be inserted successfully.");
  }

  @Test
  @DisplayName("단건 티켓을 예매할 경우, 예매가 성공한다.")
  void testBookTicket_singleTicketSuccess() {
    // given
    TicketRequest ticketRequest = new TicketRequest();
    ticketRequest.setSeatId(Collections.singletonList(1L));
    ticketRequest.setDeviceId("deviceID");

    // when
    List<TicketResponse> responses = ticketService.bookTicket("test1@gmail.com", ticketRequest);

    // then
    assertEquals(1, responses.size(), "One ticket response should be returned.");
    Long ticketCount = jdbcTemplate.queryForObject(
      "select count(*) from ticket where seat_id = 1 and member_id = 1",
      Long.class
    );
    assertEquals(1, ticketCount, "One ticket should be recorded in the database.");
  }

  @Test
  @DisplayName("2명의 사용자가 동시에 티켓을 예매할 경우, 단 1건의 예매만 성공한다.")
  void testBookTicket_multipleUsersSingleSeatSuccess2() throws InterruptedException {

    int threadCount = 2;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    List<Exception> exceptions = new ArrayList<>();

    // 첫 번째 사용자
    String email1 = "test1@gmail.com";
    TicketRequest request1 = new TicketRequest();
    request1.setDeviceId("deviceID1");
    request1.setSeatId(List.of(1L));

    // 두 번째 사용자
    String email2 = "test2@gmail.com";
    TicketRequest request2 = new TicketRequest();
    request1.setDeviceId("deviceID2");
    request1.setSeatId(List.of(1L));

    // 두 명의 사용자 각각을 별도의 스레드로 실행
    executor.submit(() -> {
      try {
        latch.countDown();
        latch.await();
        ticketService.bookTicket(email1, request1);
      } catch (Exception e) {
        System.out.println("Exception occurred for " + email1 + ": " + e.getMessage());
        exceptions.add(e);
      }
    });

    executor.submit(() -> {
      try {
        latch.countDown();
        latch.await();
        ticketService.bookTicket(email2, request2);
      } catch (Exception e) {
        System.out.println("Exception occurred for " + email2 + ": " + e.getMessage());
        exceptions.add(e);
      }
    });

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.MINUTES);

    // Check the number of tickets created in the ticket table
    String sql = "select count(*) from ticket";
    Integer ticketCount = jdbcTemplate.queryForObject(sql, Integer.class);
    assertEquals(1, ticketCount.intValue());

    // 예외 처리 내용을 확인하거나 로깅
    if (!exceptions.isEmpty()) {
      System.out.println("Exceptions occurred during ticket booking:");
      for (Exception e : exceptions) {
        System.out.println(e.getMessage());
      }
    }
  }
}
