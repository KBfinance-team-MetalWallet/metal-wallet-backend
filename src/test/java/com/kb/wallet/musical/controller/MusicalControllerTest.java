package com.kb.wallet.musical.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

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
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.seat.dto.response.SectionAvailability;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
  private MusicalSeatAvailabilityResponse seatAvailabilityResponse;
  private MusicalScheduleSeatAvailabilityResponse scheduleSeatResponse;

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

    // MusicalSeatAvailabilityResponse 설정
    seatAvailabilityResponse = new MusicalSeatAvailabilityResponse(1L, LocalTime.of(14, 30));
    seatAvailabilityResponse.setActorNames(Arrays.asList("배우1", "배우2"));
    List<SectionAvailability> sections = new ArrayList<>(); // SectionAvailability 객체들 추가 필요
    seatAvailabilityResponse.setSections(sections);

    // MusicalScheduleSeatAvailabilityResponse 설정
    scheduleSeatResponse = new MusicalScheduleSeatAvailabilityResponse(1L, 1, Grade.R);
  }

  @Nested
  @DisplayName("Musical 조회 API 테스트")
  class GetMusicalTest {

    @Test
    @DisplayName("ID로 뮤지컬을 성공적으로 조회할 수 있다")
    void getMusicalById_Success() {
      // given
      when(musicalService.getMusicalById(1L)).thenReturn(musical);

      // when
      ApiResponse<MusicalDetailResponse> response = musicalController.getMusicalById(1L);

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .satisfies(result -> {
              assertThat(result.getId())
                .as("Musical ID should match")
                .isEqualTo(musical.getId());
              assertThat(result.getTitle())
                .as("Musical title should match")
                .isEqualTo(musical.getTitle());
              assertThat(result.getPlace())
                .as("Musical place should match")
                .isEqualTo(musical.getPlace());
              assertThat(result.getPlaceDetail())
                .as("Musical place detail should match")
                .isEqualTo(musical.getPlaceDetail());
            });
        });
    }

    @Test
    @DisplayName("커서 없이 뮤지컬 목록을 조회할 수 있다")
    void getMusicals_WithoutCursor() {
      // given
      List<MusicalResponse> musicals = new ArrayList<>();
      musicals.add(MusicalResponse.convertToResponse(musical));
      when(musicalService.getMusicalsWithLimit(10)).thenReturn(musicals);

      // when
      ApiResponse<CursorResponse<MusicalResponse>> response =
        musicalController.getMusicals(null, 10);

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .satisfies(result -> {
              assertThat(result.getData())
                .asList()
                .as("Result data should not be empty")
                .isNotEmpty()
                .hasSize(1)
                .first()
                .satisfies(firstMusical -> {
                  assertThat(((MusicalResponse) firstMusical).getId())
                    .as("Musical ID should match")
                    .isEqualTo(musical.getId());
                  assertThat(((MusicalResponse) firstMusical).getTitle())
                    .as("Musical title should match")
                    .isEqualTo(musical.getTitle());
                });
            });
        });
    }

    @Test
    @DisplayName("커서를 이용하여 다음 뮤지컬 목록을 조회할 수 있다")
    void getMusicals_WithCursor() {
      // given
      List<MusicalResponse> musicals = new ArrayList<>();
      musicals.add(MusicalResponse.convertToResponse(musical));
      when(musicalService.getMusicalsAfterCursor(1L, 10)).thenReturn(musicals);

      // when
      ApiResponse<CursorResponse<MusicalResponse>> response =
        musicalController.getMusicals(1L, 10);

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .satisfies(result -> {
              assertThat(result.getData())
                .asList()
                .as("Result data should not be empty")
                .isNotEmpty();
              assertThat(result.getNextCursor())
                .as("Next cursor should not be null")
                .isNotNull();
            });
        });
    }
  }

  @Nested
  @DisplayName("Musical 스케줄 정보 조회 API 테스트")
  class GetScheduleTest {

    @Test
    @DisplayName("특정 날짜의 뮤지컬 스케줄 정보를 조회할 수 있다")
    void getScheduleInfos_Success() {
      // given
      List<MusicalSeatAvailabilityResponse> responses = new ArrayList<>();
      when(musicalService.getScheduleInfos(1L, LocalDate.now().toString()))
        .thenReturn(responses);

      // when
      ApiResponse<List<MusicalSeatAvailabilityResponse>> response =
        musicalController.getScheduleInfos(member, 1L, LocalDate.now().toString());

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);
          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull();
        });
    }

    @Test
    @DisplayName("뮤지컬의 전체 스케줄 날짜를 조회할 수 있다")
    void getScheduleDates_Success() {
      // given
      List<String> dates = new ArrayList<>();
      dates.add("2024-10-23");
      MusicalScheduleResponse responseMock = MusicalScheduleResponse.builder()
        .musicalId(1L)
        .scheduleDate(dates)
        .build();
      when(musicalService.getScheduleDates(1L)).thenReturn(responseMock);

      // when
      ApiResponse<MusicalScheduleResponse> response = musicalController.getScheduleDates(
        member, 1L);

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .satisfies(result -> {
              assertThat(result.getScheduleDate())
                .as("Schedule dates should match")
                .asList()
                .hasSize(1)
                .contains("2024-10-23");
              assertThat(result.getMusicalId())
                .as("Musical ID should match")
                .isEqualTo(1L);
            });
        });
    }
  }

  @Nested
  @DisplayName("Musical 좌석 가용성 API 테스트")
  class SeatAvailabilityTest {

    @Test
    @DisplayName("특정 날짜의 뮤지컬 좌석 가용성 정보를 상세하게 조회할 수 있다")
    void getScheduleInfos_DetailedValidation() {
      // given
      List<MusicalSeatAvailabilityResponse> responses = Arrays.asList(seatAvailabilityResponse);
      when(musicalService.getScheduleInfos(1L, LocalDate.now().toString()))
        .thenReturn(responses);

      // when
      ApiResponse<List<MusicalSeatAvailabilityResponse>> response =
        musicalController.getScheduleInfos(member, 1L, LocalDate.now().toString());

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .asList()
            .as("Result should contain exactly one response")
            .hasSize(1)
            .first()
            .satisfies(firstResponse -> {
              assertThat(((MusicalSeatAvailabilityResponse) firstResponse).getScheduleId())
                .as("Schedule ID should match")
                .isEqualTo(1L);
              assertThat(((MusicalSeatAvailabilityResponse) firstResponse).getTime())
                .as("Time should match")
                .isEqualTo("14:30");
              assertThat(((MusicalSeatAvailabilityResponse) firstResponse).getActorNames())
                .as("Actor names should be present")
                .asList()
                .hasSize(2)
                .contains("배우1", "배우2");
              assertThat(((MusicalSeatAvailabilityResponse) firstResponse).getSections())
                .as("Sections should not be null")
                .isNotNull();
            });
        });
    }

    @Test
    @DisplayName("특정 스케줄의 좌석 가용성 정보를 상세하게 조회할 수 있다")
    void getAvailableSeats_DetailedValidation() {
      // given
      List<MusicalScheduleSeatAvailabilityResponse> responses =
        Arrays.asList(scheduleSeatResponse);
      when(musicalService.getAvailableSeats(1L)).thenReturn(responses);

      // when
      ApiResponse<List<MusicalScheduleSeatAvailabilityResponse>> response =
        musicalController.getAvailableSeats(member, 1L);

      // then
      assertThat(response)
        .as("Response should not be null")
        .isNotNull()
        .satisfies(r -> {
          assertThat(r.getResultCode())
            .as("Status code should be 200")
            .isEqualTo(200);

          assertThat(r.getResult())
            .as("Result should not be null")
            .isNotNull()
            .asList()
            .as("Result should contain exactly one response")
            .hasSize(1)
            .first()
            .satisfies(firstResponse -> {
              assertThat(((MusicalScheduleSeatAvailabilityResponse) firstResponse).getSeatId())
                .as("Seat ID should match")
                .isEqualTo(1L);
              assertThat(((MusicalScheduleSeatAvailabilityResponse) firstResponse).getSeatNo())
                .as("Seat number should match")
                .isEqualTo(1);
              assertThat(((MusicalScheduleSeatAvailabilityResponse) firstResponse).getGrade())
                .as("Seat grade should match")
                .isEqualTo(Grade.R);
            });
        });
    }
  }
}