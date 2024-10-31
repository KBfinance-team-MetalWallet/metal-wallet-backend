package com.kb.wallet.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.BDDMockito.given;

import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

  @InjectMocks
  private ScheduleServiceImpl scheduleService;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Test
  @DisplayName("musicalId로 Schedule 조회 성공")
  void testGetScheduleDatesByMusicalId() {
    // given
    long musicalId = 1L;
    List<Schedule> schedules = List.of(
        Schedule.builder().id(musicalId).date(LocalDate.of(2024, 1, 1)).build(),
        Schedule.builder().id(musicalId).date(LocalDate.of(2024, 1, 2)).build(),
        Schedule.builder().id(musicalId).date(LocalDate.of(2024, 1, 3)).build()
    );

    Set<String> expectedSet = schedules.stream()
        .map(schedule -> schedule.getDate().toString())
        .collect(Collectors.toSet());

    given(scheduleRepository.findByMusicalId(musicalId)).willReturn(schedules);

    // when
    Set<String> result = scheduleService.getScheduleDatesByMusicalId(musicalId);

    // then
    assertEquals(expectedSet, result);
  }

  @Test
  @DisplayName("스케쥴이 없는 뮤지컬 ID로 조회 시 빈 Set 반환")
  void testGetScheduleDatesByMusicalId_EmptySetSuccess() {
    // given
    Long musicalId = 2L;
    given(scheduleRepository.findByMusicalId(musicalId)).willReturn(List.of());

    // when
    Set<String> result = scheduleService.getScheduleDatesByMusicalId(musicalId);

    // then
    assertTrue(result.isEmpty());
  }

}