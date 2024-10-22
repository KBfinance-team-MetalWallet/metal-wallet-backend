package com.kb.wallet.musical.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("Musical Repository 테스트")
class MusicalRepositoryTest {

  @Mock
  private MusicalRepository musicalRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("페이징 처리된 모든 뮤지컬을 조회할 수 있다")
  void testFindAll() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    Musical musical1 = Musical.builder()
      .id(1L)
      .title("Musical 1")
      .ranking(1)
      .place("Place 1")
      .placeDetail("Place Detail 1")
      .ticketingStartDate(LocalDate.of(2024, 1, 1))
      .ticketingEndDate(LocalDate.of(2024, 12, 31))
      .runningTime(120)
      .posterImageUrl("http://example.com/poster1.jpg")
      .noticeImageUrl("http://example.com/notice1.jpg")
      .detailImageUrl("http://example.com/detail1.jpg")
      .placeImageUrl("http://example.com/place1.jpg")
      .build();

    Musical musical2 = Musical.builder()
      .id(2L)
      .title("Musical 2")
      .ranking(2)
      .place("Place 2")
      .placeDetail("Place Detail 2")
      .ticketingStartDate(LocalDate.of(2024, 2, 1))
      .ticketingEndDate(LocalDate.of(2024, 11, 30))
      .runningTime(150)
      .posterImageUrl("http://example.com/poster2.jpg")
      .noticeImageUrl("http://example.com/notice2.jpg")
      .detailImageUrl("http://example.com/detail2.jpg")
      .placeImageUrl("http://example.com/place2.jpg")
      .build();

    Page<Musical> page = new PageImpl<>(Arrays.asList(musical1, musical2));
    when(musicalRepository.findAll(pageable)).thenReturn(page);

    // when
    Page<Musical> result = musicalRepository.findAll(pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent())
      .hasSize(2)
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
        tuple(
          1L,
          "Musical 1",
          1,
          "Place 1",
          "Place Detail 1",
          LocalDate.of(2024, 1, 1),
          LocalDate.of(2024, 12, 31),
          120,
          "http://example.com/poster1.jpg",
          "http://example.com/notice1.jpg",
          "http://example.com/detail1.jpg",
          "http://example.com/place1.jpg"
        ),
        tuple(
          2L,
          "Musical 2",
          2,
          "Place 2",
          "Place Detail 2",
          LocalDate.of(2024, 2, 1),
          LocalDate.of(2024, 11, 30),
          150,
          "http://example.com/poster2.jpg",
          "http://example.com/notice2.jpg",
          "http://example.com/detail2.jpg",
          "http://example.com/place2.jpg"
        )
      );

    verify(musicalRepository, times(1)).findAll(pageable);
  }

  @Test
  @DisplayName("ID로 뮤지컬을 조회할 수 있다")
  void testFindById() {
    // given
    Musical musical = Musical.builder()
      .id(1L)
      .title("Musical 1")
      .ranking(1)
      .place("Place 1")
      .placeDetail("Place Detail 1")
      .ticketingStartDate(LocalDate.of(2024, 1, 1))
      .ticketingEndDate(LocalDate.of(2024, 12, 31))
      .runningTime(120)
      .posterImageUrl("http://example.com/poster1.jpg")
      .noticeImageUrl("http://example.com/notice1.jpg")
      .detailImageUrl("http://example.com/detail1.jpg")
      .placeImageUrl("http://example.com/place1.jpg")
      .build();

    when(musicalRepository.findById(1L)).thenReturn(Optional.of(musical));

    // when
    Optional<Musical> result = musicalRepository.findById(1L);

    // then
    assertThat(result)
      .isPresent()
      .get()
      .satisfies(m -> {
        assertThat(m)
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
            "Place Detail 1",
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            120,
            "http://example.com/poster1.jpg",
            "http://example.com/notice1.jpg",
            "http://example.com/detail1.jpg",
            "http://example.com/place1.jpg"
          );
      });

    verify(musicalRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("랭킹 순으로 정렬된 뮤지컬 목록을 조회할 수 있다")
  void testFindAllByRankingAsc() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    Musical musical1 = Musical.builder()
      .id(1L)
      .title("Musical 1")
      .ranking(1)
      .place("Place 1")
      .placeDetail("Place Detail 1")
      .ticketingStartDate(LocalDate.of(2024, 1, 1))
      .ticketingEndDate(LocalDate.of(2024, 12, 31))
      .runningTime(120)
      .posterImageUrl("http://example.com/poster1.jpg")
      .noticeImageUrl("http://example.com/notice1.jpg")
      .detailImageUrl("http://example.com/detail1.jpg")
      .placeImageUrl("http://example.com/place1.jpg")
      .build();

    Musical musical2 = Musical.builder()
      .id(2L)
      .title("Musical 2")
      .ranking(2)
      .place("Place 2")
      .placeDetail("Place Detail 2")
      .ticketingStartDate(LocalDate.of(2024, 2, 1))
      .ticketingEndDate(LocalDate.of(2024, 11, 30))
      .runningTime(150)
      .posterImageUrl("http://example.com/poster2.jpg")
      .noticeImageUrl("http://example.com/notice2.jpg")
      .detailImageUrl("http://example.com/detail2.jpg")
      .placeImageUrl("http://example.com/place2.jpg")
      .build();

    List<Musical> musicals = Arrays.asList(musical1, musical2);
    when(musicalRepository.findAllByRankingAsc(pageable)).thenReturn(musicals);

    // when
    List<Musical> result = musicalRepository.findAllByRankingAsc(pageable);

    // then
    assertThat(result)
      .hasSize(2)
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
        tuple(
          1L,
          "Musical 1",
          1,
          "Place 1",
          "Place Detail 1",
          LocalDate.of(2024, 1, 1),
          LocalDate.of(2024, 12, 31),
          120,
          "http://example.com/poster1.jpg",
          "http://example.com/notice1.jpg",
          "http://example.com/detail1.jpg",
          "http://example.com/place1.jpg"
        ),
        tuple(
          2L,
          "Musical 2",
          2,
          "Place 2",
          "Place Detail 2",
          LocalDate.of(2024, 2, 1),
          LocalDate.of(2024, 11, 30),
          150,
          "http://example.com/poster2.jpg",
          "http://example.com/notice2.jpg",
          "http://example.com/detail2.jpg",
          "http://example.com/place2.jpg"
        )
      );

    verify(musicalRepository, times(1)).findAllByRankingAsc(pageable);
  }

  @Test
  @DisplayName("커서 기반으로 다음 뮤지컬 목록을 조회할 수 있다")
  void testFindAllAfterCursor() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    Musical musical3 = Musical.builder()
      .id(3L)
      .title("Musical 3")
      .ranking(3)
      .place("Place 3")
      .placeDetail("Place Detail 3")
      .ticketingStartDate(LocalDate.of(2024, 1, 1))
      .ticketingEndDate(LocalDate.of(2024, 12, 31))
      .runningTime(120)
      .posterImageUrl("http://example.com/poster3.jpg")
      .noticeImageUrl("http://example.com/notice3.jpg")
      .detailImageUrl("http://example.com/detail3.jpg")
      .placeImageUrl("http://example.com/place3.jpg")
      .build();

    Musical musical4 = Musical.builder()
      .id(4L)
      .title("Musical 4")
      .ranking(4)
      .place("Place 4")
      .placeDetail("Place Detail 4")
      .ticketingStartDate(LocalDate.of(2024, 2, 1))
      .ticketingEndDate(LocalDate.of(2024, 11, 30))
      .runningTime(150)
      .posterImageUrl("http://example.com/poster4.jpg")
      .noticeImageUrl("http://example.com/notice4.jpg")
      .detailImageUrl("http://example.com/detail4.jpg")
      .placeImageUrl("http://example.com/place4.jpg")
      .build();

    List<Musical> musicals = Arrays.asList(musical3, musical4);
    when(musicalRepository.findAllAfterCursor(3L, pageable)).thenReturn(musicals);

    // when
    List<Musical> result = musicalRepository.findAllAfterCursor(3L, pageable);

    // then
    assertThat(result)
      .hasSize(2)
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
        tuple(
          3L,
          "Musical 3",
          3,
          "Place 3",
          "Place Detail 3",
          LocalDate.of(2024, 1, 1),
          LocalDate.of(2024, 12, 31),
          120,
          "http://example.com/poster3.jpg",
          "http://example.com/notice3.jpg",
          "http://example.com/detail3.jpg",
          "http://example.com/place3.jpg"
        ),
        tuple(
          4L,
          "Musical 4",
          4,
          "Place 4",
          "Place Detail 4",
          LocalDate.of(2024, 2, 1),
          LocalDate.of(2024, 11, 30),
          150,
          "http://example.com/poster4.jpg",
          "http://example.com/notice4.jpg",
          "http://example.com/detail4.jpg",
          "http://example.com/place4.jpg"
        )
      );

    verify(musicalRepository, times(1)).findAllAfterCursor(3L, pageable);
  }
}