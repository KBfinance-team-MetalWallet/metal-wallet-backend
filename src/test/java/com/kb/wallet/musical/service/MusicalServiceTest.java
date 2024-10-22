package com.kb.wallet.musical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.repository.CustomMusicalRepository;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.service.ScheduleService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
  private MusicalServiceImpl musicalService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    musicalService = new MusicalServiceImpl(musicalRepository, customMusicalRepository,
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
      Musical result = musicalService.getMusicalById(1L);

      // then
      assertThat(result)
        .isNotNull()
        .extracting(
          Musical::getId,
          Musical::getTitle,
          Musical::getRanking,
          Musical::getPlace,
          Musical::getPlaceDetail,
          Musical::getTicketingStartDate,
          Musical::getTicketingEndDate,
          Musical::getRunningTime,
          Musical::getPosterImageUrl,
          Musical::getNoticeImageUrl,
          Musical::getDetailImageUrl,
          Musical::getPlaceImageUrl
        )
        .containsExactly(
          1L,
          "Musical 1",
          1,
          "Place 1",
          "Detail 1",
          LocalDate.of(2024, 1, 1),
          LocalDate.of(2024, 12, 31),
          120,
          null,
          null,
          null,
          null
        );

      verify(musicalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 Musical을 조회했을 때 존재하지 않으면 예외를 발생시킨다")
    void testGetMusicalById_notFound() {
      // given
      when(musicalRepository.findById(1L)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> musicalService.getMusicalById(1L))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MUSICAL_NOT_FOUND);

      verify(musicalRepository, times(1)).findById(1L);
    }
  }
}