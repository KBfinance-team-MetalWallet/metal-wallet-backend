package com.kb.wallet.musical.service;

import static com.kb.wallet.seat.constant.Grade.R;
import static com.kb.wallet.seat.constant.Grade.S;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.repository.CustomMusicalRepository;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.domain.Section;
import com.kb.wallet.seat.dto.response.SectionAvailability;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.service.ScheduleService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

@DisplayName("MusicalService 테스트")
class MusicalServiceTest {

  @Mock
  private MusicalRepository musicalRepository;
  @Mock
  private CustomMusicalRepository customMusicalRepository;
  @Mock
  private ScheduleService scheduleService;
  @Mock
  private SeatRepository seatRepository;
  private MusicalServiceImpl musicalServiceImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    musicalServiceImpl = new MusicalServiceImpl(musicalRepository, customMusicalRepository,
      scheduleService, seatRepository);
  }

  @Nested
  @DisplayName("Musical 조회 기능")
  class RetrieveMusicalTests {

    @Test
    @DisplayName("ID로 Musical을 조회했을 때 존재하면 반환한다")
    void testGetMusicalById_found() {
      // given
      Musical musical = Musical.builder()
        .id(1L)
        .title("Musical 1")
        .ranking(1)
        .place("Place 1")
        .placeDetail("Detail 1")
        .ticketingStartDate(LocalDate.of(2024, 1, 1))
        .ticketingEndDate(LocalDate.of(2024, 12, 31))
        .runningTime(120)
        .posterImageUrl(null)
        .noticeImageUrl(null)
        .detailImageUrl(null)
        .placeImageUrl(null)
        .build();

      when(musicalRepository.findById(1L)).thenReturn(Optional.of(musical));

      // when
      Musical result = musicalServiceImpl.getMusicalById(1L);

      // then
      assertThat(result).isEqualTo(musical);
      verify(musicalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 Musical을 조회했을 때 존재하지 않으면 예외를 발생시킨다")
    void testGetMusicalById_notFound() {
      // given
      when(musicalRepository.findById(1L)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicalServiceImpl.getMusicalById(1L))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSICAL_NOT_FOUND);

      verify(musicalRepository, times(1)).findById(1L);
    }
  }

  @Nested
  @DisplayName("Musical 목록 조회 기능")
  class RetrieveMusicalListTests {

    @Test
    @DisplayName("size 만큼의 Musical 목록을 반환한다")
    void testGetMusicalsWithLimit() {
      // given
      List<Musical> musicals = Arrays.asList(
        Musical.builder()
          .id(1L)
          .title("Musical 1")
          .ranking(1)
          .place("Place 1")
          .placeDetail("Detail 1")
          .ticketingStartDate(LocalDate.of(2024, 1, 1))
          .ticketingEndDate(LocalDate.of(2024, 12, 31))
          .runningTime(120)
          .posterImageUrl("poster1.jpg")
          .noticeImageUrl("notice1.jpg")
          .detailImageUrl("detail1.jpg")
          .placeImageUrl("place1.jpg")
          .build(),
        Musical.builder()
          .id(2L)
          .title("Musical 2")
          .ranking(2)
          .place("Place 2")
          .placeDetail("Detail 2")
          .ticketingStartDate(LocalDate.of(2024, 2, 1))
          .ticketingEndDate(LocalDate.of(2024, 12, 31))
          .runningTime(150)
          .posterImageUrl("poster2.jpg")
          .noticeImageUrl("notice2.jpg")
          .detailImageUrl("detail2.jpg")
          .placeImageUrl("place2.jpg")
          .build()
      );

      when(musicalRepository.findAllByRankingAsc(any(PageRequest.class))).thenReturn(musicals);

      // when
      List<MusicalResponse> result = musicalServiceImpl.getMusicalsWithLimit(2);

      // then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getId()).isEqualTo(1L);
      assertThat(result.get(1).getId()).isEqualTo(2L);
      verify(musicalRepository, times(1)).findAllByRankingAsc(any(PageRequest.class));
    }

    @Test
    @DisplayName("cursor 이후의 Musical 목록을 size 만큼 반환한다")
    void testGetMusicalsAfterCursor() {
      // given
      List<Musical> musicals = Arrays.asList(
        Musical.builder()
          .id(2L)
          .title("Musical 2")
          .ranking(2)
          .place("Place 2")
          .placeDetail("Detail 2")
          .ticketingStartDate(LocalDate.of(2024, 2, 1))
          .ticketingEndDate(LocalDate.of(2024, 12, 31))
          .runningTime(150)
          .posterImageUrl("poster2.jpg")
          .noticeImageUrl("notice2.jpg")
          .detailImageUrl("detail2.jpg")
          .placeImageUrl("place2.jpg")
          .build(),
        Musical.builder()
          .id(3L)
          .title("Musical 3")
          .ranking(3)
          .place("Place 3")
          .placeDetail("Detail 3")
          .ticketingStartDate(LocalDate.of(2024, 3, 1))
          .ticketingEndDate(LocalDate.of(2024, 12, 31))
          .runningTime(180)
          .posterImageUrl("poster3.jpg")
          .noticeImageUrl("notice3.jpg")
          .detailImageUrl("detail3.jpg")
          .placeImageUrl("place3.jpg")
          .build()
      );

      when(musicalRepository.findAllAfterCursor(eq(1L), any(PageRequest.class)))
        .thenReturn(musicals);

      // when
      List<MusicalResponse> result = musicalServiceImpl.getMusicalsAfterCursor(1L, 2);

      // then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getId()).isEqualTo(2L);
      assertThat(result.get(1).getId()).isEqualTo(3L);
      verify(musicalRepository, times(1))
        .findAllAfterCursor(eq(1L), any(PageRequest.class));
    }
  }

  @Nested
  @DisplayName("Musical 스케줄 조회 기능")
  class RetrieveMusicalScheduleTests {

    @Test
    @DisplayName("Musical ID와 날짜로 스케줄 정보를 조회한다")
    void testGetScheduleInfos() {
      // given
      LocalDate date = LocalDate.of(2024, 1, 1);
      List<MusicalSeatAvailabilityResponse> expectedResponses = Arrays.asList(
        createMusicalSeatAvailabilityResponse(1L, LocalTime.of(14, 0)),
        createMusicalSeatAvailabilityResponse(2L, LocalTime.of(19, 0))
      );

      when(customMusicalRepository.findMusicalSeatAvailability(1L, date))
        .thenReturn(expectedResponses);

      // when
      List<MusicalSeatAvailabilityResponse> result = musicalServiceImpl
        .getScheduleInfos(1L, "2024-01-01");

      // then
      assertThat(result).isEqualTo(expectedResponses);
      verify(customMusicalRepository, times(1))
        .findMusicalSeatAvailability(1L, date);
    }

    private MusicalSeatAvailabilityResponse createMusicalSeatAvailabilityResponse(
      long scheduleId, LocalTime time) {
      MusicalSeatAvailabilityResponse response = new MusicalSeatAvailabilityResponse(scheduleId,
        time);
      response.setActorNames(Arrays.asList("Actor 1", "Actor 2"));
      response.setSections(Arrays.asList(
        new SectionAvailability(R, 100000),
        new SectionAvailability(S, 80000)
      ));
      return response;
    }

    @Test
    @DisplayName("Musical ID로 스케줄 날짜들을 조회한다")
    void testGetScheduleDates() {
      // given
      Set<String> expectedDates = new HashSet<>(Arrays.asList("2024-01-01", "2024-01-02"));
      when(scheduleService.getScheduleDatesByMusicalId(1L)).thenReturn(expectedDates);

      // when
      Set<String> result = musicalServiceImpl.getScheduleDates(1L);

      // then
      assertThat(result).isEqualTo(expectedDates);
      verify(scheduleService, times(1)).getScheduleDatesByMusicalId(1L);
    }

    @Test
    @DisplayName("스케줄 ID로 이용 가능한 좌석 목록을 조회한다")
    void testGetAvailableSeats() {
      // given
      Schedule schedule = Schedule.builder()
        .id(1L)
        .build();

      Section vipSection = new Section(
        1L,
        Musical.builder().id(1L).build(),
        schedule,
        R,
        100000,
        50
      );

      List<Seat> seats = Arrays.asList(
        Seat.builder()
          .id(1L)
          .seatNo(1)
          .section(vipSection)
          .schedule(schedule)
          .isAvailable(true)
          .build(),
        Seat.builder()
          .id(2L)
          .seatNo(2)
          .section(vipSection)
          .schedule(schedule)
          .isAvailable(true)
          .build()
      );

      when(seatRepository.findAvailableSeatsByScheduleId(anyLong())).thenReturn(seats);

      // when
      List<MusicalScheduleSeatAvailabilityResponse> result = musicalServiceImpl
        .getAvailableSeats(1L);

      // then
      assertThat(result).hasSize(2);

      // 첫 번째 좌석 검증
      assertThat(result.get(0))
        .satisfies(response -> {
          assertThat(response.getSeatId()).isEqualTo(1L);
          assertThat(response.getSeatNo()).isEqualTo(1);
          assertThat(response.getGrade()).isEqualTo(R);
        });

      // 두 번째 좌석 검증
      assertThat(result.get(1))
        .satisfies(response -> {
          assertThat(response.getSeatId()).isEqualTo(2L);
          assertThat(response.getSeatNo()).isEqualTo(2);
          assertThat(response.getGrade()).isEqualTo(R);
        });

      verify(seatRepository, times(1)).findAvailableSeatsByScheduleId(1L);
    }
  }
}