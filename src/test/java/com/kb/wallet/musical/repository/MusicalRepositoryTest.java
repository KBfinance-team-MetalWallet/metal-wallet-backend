package com.kb.wallet.musical.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.kb.wallet.global.config.TestConfig;
import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@ActiveProfiles("test")
@DisplayName("Musical Repository 테스트")
class MusicalRepositoryTest {

  @Autowired
  private MusicalRepository musicalRepository;

  private Musical musical1;
  private Musical musical2;

  @BeforeEach
  void setUp() {
    musicalRepository.deleteAll();

    musical1 = Musical.builder()
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

    musical2 = Musical.builder()
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

    musicalRepository.save(musical1);
    musicalRepository.save(musical2);
  }

  @Test
  @DisplayName("페이징 처리된 모든 뮤지컬을 조회할 수 있다")
  void testFindAll() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<Musical> result = musicalRepository.findAll(pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent())
      .hasSize(2)
      .extracting(
        Musical::getTitle,
        Musical::getRanking,
        Musical::getPlace
      )
      .containsExactly(
        tuple("Musical 1", 1, "Place 1"),
        tuple("Musical 2", 2, "Place 2")
      );
  }


  @Test
  @DisplayName("ID로 뮤지컬을 조회할 수 있다")
  void testFindById() {
    // when
    Optional<Musical> result = musicalRepository.findById(musical1.getId());

    // then
    assertThat(result)
      .isPresent()
      .get()
      .satisfies(m -> {
        assertThat(m)
          .extracting(
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
  }

  @Test
  @DisplayName("랭킹 순으로 정렬된 뮤지컬 목록을 조회할 수 있다")
  void testFindAllByRankingAsc() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<Musical> result = musicalRepository.findAllByRankingAsc(pageable);

    // then
    assertThat(result)
      .hasSize(2)
      .extracting(
        Musical::getTitle,
        Musical::getRanking,
        Musical::getPlace
      )
      .containsExactly(
        tuple("Musical 1", 1, "Place 1"),
        tuple("Musical 2", 2, "Place 2")
      );
  }

  @Test
  @DisplayName("커서 기반으로 다음 뮤지컬 목록을 조회할 수 있다")
  void testFindAllAfterCursor() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    Musical musical3 = Musical.builder()
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
    musicalRepository.save(musical3);

    // when
    List<Musical> result = musicalRepository.findAllAfterCursor(musical1.getId(), pageable);

    // then
    assertThat(result)
      .hasSize(2)
      .extracting(
        Musical::getTitle,
        Musical::getRanking,
        Musical::getPlace
      )
      .containsExactly(
        tuple("Musical 2", 2, "Place 2"),
        tuple("Musical 3", 3, "Place 3")
      );
  }
}