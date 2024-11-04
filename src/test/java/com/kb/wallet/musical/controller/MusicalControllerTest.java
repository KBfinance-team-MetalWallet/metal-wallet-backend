package com.kb.wallet.musical.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.response.MusicalDetailResponse;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.service.MusicalService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Musical Controller 테스트")
@ExtendWith(MockitoExtension.class)
class MusicalControllerTest {

  @Mock
  private MusicalService musicalService;

  @InjectMocks
  private MusicalController musicalController;

  @Mock
  private Member member;

  private Musical musical;

  @BeforeEach
  void setUp() {
    musical = Musical.builder()
      .id(1L)
      .title("뮤지컬 제목")
      .ranking(1)
      .place("장소")
      .placeDetail("상세 장소")
      .ticketingStartDate(LocalDate.now())
      .ticketingEndDate(LocalDate.now().plusDays(30))
      .runningTime(120)
      .posterImageUrl("포스터 이미지 URL")
      .noticeImageUrl("공지 이미지 URL")
      .detailImageUrl("상세 이미지 URL")
      .placeImageUrl("장소 이미지 URL")
      .build();
  }

  @Nested
  @DisplayName("Musical 조회 API 테스트")
  class GetMusicalTest {

    @Test
    @DisplayName("ID로 뮤지컬을 성공적으로 조회할 수 있다")
    void getMusicalById_Success() {
      when(musicalService.getMusicalById(1L)).thenReturn(musical);

      ApiResponse<MusicalDetailResponse> response = musicalController.getMusicalById(1L);

      assertEquals("Status code should be 200", 200, response.getResultCode());
      assertNotNull(response.getResult());
      assertEquals("Musical ID should match", musical.getId(), response.getResult().getId());
    }

    @Test
    @DisplayName("커서 없이 뮤지컬 목록을 조회할 수 있다")
    void getMusicals_WithoutCursor() {
      // given
      List<MusicalResponse> musicals = new ArrayList<>();
      musicals.add(MusicalResponse.convertToResponse(musical));

      // musicalService.getMusicalsWithLimit()는 List<MusicalResponse>를 반환
      when(musicalService.getMusicalsWithLimit(10)).thenReturn(musicals);

      // when
      ApiResponse<CursorResponse<MusicalResponse>> response = musicalController.getMusicals(null,
        10);

      // then
      assertEquals("Status code should be 200", 200, response.getResultCode());
      assertNotNull(response.getResult());
      assertFalse("Result data should not be empty", response.getResult().getData().isEmpty());

      // 데이터 검증 추가
      MusicalResponse firstMusical = response.getResult().getData().get(0);
      assertEquals("Musical title should match", musical.getTitle(), firstMusical.getTitle());
      assertEquals("Musical id should match", musical.getId(), firstMusical.getId());
    }

    @Test
    @DisplayName("커서를 이용하여 다음 뮤지컬 목록을 조회할 수 있다")
    void getMusicals_WithCursor() {
      List<MusicalResponse> musicals = new ArrayList<>();
      musicals.add(MusicalResponse.convertToResponse(musical));
      when(musicalService.getMusicalsAfterCursor(1L, 10)).thenReturn(musicals);

      ApiResponse<CursorResponse<MusicalResponse>> response = musicalController.getMusicals(1L, 10);

      assertEquals("Status code should be 200", 200, response.getResultCode());
      assertNotNull(response.getResult());
      assertFalse("Result data should not be empty", response.getResult().getData().isEmpty());
    }
  }

  @Nested
  @DisplayName("Musical 스케줄 정보 조회 API 테스트")
  class GetScheduleTest {

    @Test
    @DisplayName("특정 날짜의 뮤지컬 스케줄 정보를 조회할 수 있다")
    void getScheduleInfos_Success() {
      List<MusicalSeatAvailabilityResponse> responses = new ArrayList<>();
      when(musicalService.getScheduleInfos(1L, LocalDate.now().toString())).thenReturn(responses);

      ApiResponse<List<MusicalSeatAvailabilityResponse>> response = musicalController.getScheduleInfos(
        member, 1L, LocalDate.now().toString());

      assertEquals("Status code should be 200", 200, response.getResultCode());
      assertNotNull(response.getResult());
      assertTrue("Result data should be empty", response.getResult().isEmpty());
    }

    @Test
    @DisplayName("뮤지컬의 전체 스케줄 날짜를 조회할 수 있다")
    void getScheduleDates_Success() {
      List<String> dates = new ArrayList<>();
      dates.add("2024-10-23");
      MusicalScheduleResponse responseMock = MusicalScheduleResponse.builder()
        .musicalId(1L)
        .scheduleDate(dates)
        .build();
      when(musicalService.getScheduleDates(1L)).thenReturn(responseMock);

      ApiResponse<MusicalScheduleResponse> response = musicalController.getScheduleDates(member,
        1L);

      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Response result should not be null")
            .isNotNull()
            .extracting("scheduleDate")
            .as("Schedule dates should match")
            .isEqualTo(dates);
        });

    }

    @Test
    @DisplayName("특정 뮤지컬의 잔여석 정보를 조회할 수 있다")
    void getAvailableSeats_Success() {
      List<MusicalScheduleSeatAvailabilityResponse> responses = new ArrayList<>();
      when(musicalService.getAvailableSeats(1L)).thenReturn(responses);

      ApiResponse<List<MusicalScheduleSeatAvailabilityResponse>> response =
        musicalController.getAvailableSeats(member, 1L);

      assertEquals("Status code should be 200", 200, response.getResultCode());
      assertNotNull(response.getResult());
      assertTrue("Result data should be empty", response.getResult().isEmpty());
    }
  }
}