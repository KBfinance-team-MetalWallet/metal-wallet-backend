package com.kb.wallet.musical.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Musical 도메인 테스트")
class MusicalDomainTest {

  private Musical musical;
  private LocalDate startDate;
  private LocalDate endDate;

  @BeforeEach
  void setUp() {
    startDate = LocalDate.of(2024, 1, 1);
    endDate = LocalDate.of(2024, 12, 31);

    musical = new Musical(
      1L,
      "오페라의 유령",
      1,
      "샤롯데씨어터",
      "서울특별시 송파구",
      startDate,
      endDate,
      180,
      "http://example.com/poster.jpg",
      "http://example.com/notice.jpg",
      "http://example.com/detail.jpg",
      "http://example.com/place.jpg"
    );
  }

  @Nested
  @DisplayName("Musical 생성 테스트")
  class CreateMusicalTest {

    @Test
    @DisplayName("모든 필드가 포함된 Musical 객체를 생성할 수 있다")
    void createMusicalWithAllFields() {
      assertThat(musical)
        .satisfies(m -> {
          assertThat(m.getId()).isEqualTo(1L);  // 의도적으로 실패하도록 2L로 설정
          assertThat(m.getTitle()).isEqualTo("오페라의 유령");
          assertThat(m.getRanking()).isEqualTo(1);
          assertThat(m.getPlace()).isEqualTo("샤롯데씨어터");
          assertThat(m.getPlaceDetail()).isEqualTo("서울특별시 송파구");
          assertThat(m.getTicketingStartDate()).isEqualTo(startDate);
          assertThat(m.getTicketingEndDate()).isEqualTo(endDate);
          assertThat(m.getRunningTime()).isEqualTo(180);
        });

      // URL 필드들 검증
      assertThat(musical)
        .extracting(
          Musical::getPosterImageUrl,
          Musical::getNoticeImageUrl,
          Musical::getDetailImageUrl,
          Musical::getPlaceImageUrl
        )
        .containsExactly(
          "http://example.com/poster.jpg",
          "http://example.com/notice.jpg",
          "http://example.com/detail.jpg",
          "http://example.com/place.jpg"
        );
    }

    @Test
    @DisplayName("필수 필드만으로 Musical 객체를 생성할 수 있다")
    void createMusicalWithRequiredFieldsOnly() {
      // given
      Musical musicalWithRequiredFields = new Musical(
        1L,
        "오페라의 유령",
        1,
        "샤롯데씨어터",
        "서울특별시 송파구",
        startDate,
        endDate,
        180,
        null,
        null,
        null,
        null
      );

      // then
      assertThat(musicalWithRequiredFields)
        .satisfies(m -> {
          assertThat(m.getTitle()).isEqualTo("오페라의 유령");
          assertThat(m.getRanking()).isEqualTo(1);
          assertThat(m.getPlace()).isEqualTo("샤롯데씨어터");
          assertThat(m.getPlaceDetail()).isEqualTo("서울특별시 송파구");
          assertThat(m.getTicketingStartDate()).isEqualTo(startDate);
          assertThat(m.getTicketingEndDate()).isEqualTo(endDate);
          assertThat(m.getRunningTime()).isEqualTo(180);
        });

      // URL 필드들은 모두 null인지 검증
      assertThat(musicalWithRequiredFields)
        .extracting(
          Musical::getPosterImageUrl,
          Musical::getNoticeImageUrl,
          Musical::getDetailImageUrl,
          Musical::getPlaceImageUrl
        )
        .containsOnlyNulls();
    }

    @Test
    @DisplayName("NoArgsConstructor로 생성한 후 필드를 설정할 수 있다")
    void createMusicalWithNoArgsConstructor() {
      // given
      Musical emptyMusical = new Musical();

      // then
      assertThat(emptyMusical)
        .satisfies(m -> {
          assertThat(m.getId()).isNull();
          assertThat(m.getTitle()).isNull();
          assertThat(m.getRanking()).isZero();
          assertThat(m.getPlace()).isNull();
          assertThat(m.getPlaceDetail()).isNull();
          assertThat(m.getTicketingStartDate()).isNull();
          assertThat(m.getTicketingEndDate()).isNull();
          assertThat(m.getRunningTime()).isZero();
        });

      // URL 필드들은 모두 null인지 검증
      assertThat(emptyMusical)
        .extracting(
          Musical::getPosterImageUrl,
          Musical::getNoticeImageUrl,
          Musical::getDetailImageUrl,
          Musical::getPlaceImageUrl
        )
        .containsOnlyNulls();
    }
  }
}