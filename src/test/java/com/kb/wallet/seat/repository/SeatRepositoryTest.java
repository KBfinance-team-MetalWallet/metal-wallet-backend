package com.kb.wallet.seat.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.domain.Section;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@WebAppConfiguration
class SeatRepositoryTest {

  @Autowired
  private MusicalRepository musicalRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private SectionRepository sectionRepository;

  @Autowired
  private SeatRepository seatRepository;

  @Test
  @DisplayName("스케줄 ID에 해당하는 예매 가능한 좌석 정보 조회 성공")
  void testFindAvailableSeatsByScheduleId() {
    // given
    Musical musical = Musical.builder().title("test")
        .ranking(1)
        .place("seoul")
        .placeDetail("seoung-su")
        .ticketingStartDate(LocalDate.now())
        .ticketingEndDate(LocalDate.now().plus(Period.ofDays(1)))
        .build();
    musicalRepository.save(musical);

    Schedule schedule = Schedule.builder()
        .date(LocalDate.of(2024, 12, 1))
        .musical(musical)
        .build();
    scheduleRepository.save(schedule);

    Section section = Section.builder()
        .grade(Grade.R)
        .build();

    sectionRepository.save(section);

    Seat availableSeat = Seat.builder()
        .seatNo(1)
        .isAvailable(true)
        .schedule(schedule)
        .section(section)
        .build();

    Seat unavailableSeat = Seat.builder()
        .seatNo(2)
        .isAvailable(false)
        .schedule(schedule)
        .section(section)
        .build();

    seatRepository.save(availableSeat);
    seatRepository.save(unavailableSeat);

    // when
    List<Seat> availableSeats = seatRepository.findAvailableSeatsByScheduleId(schedule.getId());

    // then
    assertThat(availableSeats).isNotEmpty();
    assertThat(availableSeats.size()).isEqualTo(1);
    assertThat(availableSeats.get(0).getSeatNo()).isEqualTo(1);
    assertThat(availableSeats.get(0).isAvailable()).isTrue();
  }
}