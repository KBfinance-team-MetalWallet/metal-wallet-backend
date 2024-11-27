package com.kb.wallet.ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.ticket.domain.Schedule;
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
class ScheduleRepositoryTest {

  @Autowired
  private MusicalRepository musicalRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Test
  @DisplayName("뮤지컬 ID로 스케줄 조회 성공")
  void testFindByMusicalId_Success() {
    // given
    Musical musical = Musical.builder().title("test")
        .ranking(1)
        .place("seoul")
        .placeDetail("seoung-su")
        .ticketingStartDate(LocalDate.now())
        .ticketingEndDate(LocalDate.now().plus(Period.ofDays(1)))
        .build();
    musicalRepository.save(musical);

    Schedule schedule1 = Schedule.builder()
        .date(LocalDate.of(2024, 12, 1))
        .musical(musical)
        .build();

    Schedule schedule2 = Schedule.builder()
        .date(LocalDate.of(2024, 12, 2))
        .musical(musical)
        .build();

    scheduleRepository.save(schedule1);
    scheduleRepository.save(schedule2);

    // when
    List<Schedule> schedules = scheduleRepository.findByMusicalId(musical.getId());

    // then
    assertThat(schedules).isNotEmpty();
    assertThat(schedules.size()).isEqualTo(2);
    assertThat(schedules.get(0).getMusical().getId()).isEqualTo(musical.getId());
    assertThat(schedules.get(1).getMusical().getId()).isEqualTo(musical.getId());
  }

}