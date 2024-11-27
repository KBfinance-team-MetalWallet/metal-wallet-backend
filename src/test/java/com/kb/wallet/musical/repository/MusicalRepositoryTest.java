package com.kb.wallet.musical.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@WebAppConfiguration
@Transactional
public class MusicalRepositoryTest {

  @Autowired
  private MusicalRepository musicalRepository;

  private Musical testMusical1;
  private Musical testMusical2;


  @BeforeEach
  void setUp() {
    musicalRepository.deleteAll();
    LocalDate now = LocalDate.now();
    int baseRanking = Math.abs(UUID.randomUUID().hashCode() % 1000000);

    testMusical1 = Musical.builder()
      .title("레미제라블")
      .ranking(baseRanking)
      .place("예술의전당")
      .placeDetail("오페라하우스")
      .ticketingStartDate(now)
      .ticketingEndDate(now.plusMonths(1))
      .runningTime(180)
      .posterImageUrl("poster1.jpg")
      .noticeImageUrl("notice1.jpg")
      .detailImageUrl("detail1.jpg")
      .placeImageUrl("place1.jpg")
      .build();

    testMusical2 = Musical.builder()
      .title("오페라의 유령")
      .ranking(baseRanking + 1)
      .place("블루스퀘어")
      .placeDetail("신한카드홀")
      .ticketingStartDate(now.plusDays(7))
      .ticketingEndDate(now.plusMonths(2))
      .runningTime(160)
      .posterImageUrl("poster2.jpg")
      .noticeImageUrl("notice2.jpg")
      .detailImageUrl("detail2.jpg")
      .placeImageUrl("place2.jpg")
      .build();
    musicalRepository.saveAll(Arrays.asList(testMusical1, testMusical2));
  }

  @Test
  @DisplayName("페이지네이션을 사용하여 모든 뮤지컬을 조회하는 테스트")
  void testFindAllWithPagination() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Musical> musicalPage = musicalRepository.findAll(pageRequest);
    List<Musical> content = musicalPage.getContent();

    // then
    assertThat(musicalPage.getTotalElements())
      .as("전체 데이터 수")
      .isEqualTo(2L);

    assertThat(musicalPage.getTotalPages())
      .as("전체 페이지 수")
      .isEqualTo(1);  // 2개의 데이터가 있고 페이지 크기가 2이므로 1페이지

    assertThat(content)
      .as("페이지 내 데이터")
      .hasSize(2)
      .extracting(
        Musical::getTitle,
        Musical::getRanking,
        Musical::getPlace
      )
      .containsExactly(
        tuple(testMusical1.getTitle(), testMusical1.getRanking(), testMusical1.getPlace()),
        tuple(testMusical2.getTitle(), testMusical2.getRanking(), testMusical2.getPlace())
      );
  }


  @Test
  @DisplayName("ID로 뮤지컬을 성공적으로 조회하는 테스트")
  void testFindById() {
    // when
    Optional<Musical> found = musicalRepository.findById(testMusical1.getId());
    // then
    assertThat(found)
      .as("ID로 조회한 뮤지컬")
      .isPresent()
      .get()
      .satisfies(musical -> {
        assertThat(musical)
          .usingRecursiveComparison()
          .ignoringFields("id")  // ID 필드는 제외하고 비교
          .isEqualTo(testMusical1);
      });
  }

  @Test
  @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환하는 테스트")
  void testFindById_NotFound() {
    // when
    Optional<Musical> foundMusical = musicalRepository.findById(999L);
    // then
    assertThat(foundMusical).isEmpty();
  }

  @Test
  @DisplayName("랭킹 순으로 정렬된 뮤지컬 목록을 조회하는 테스트")
  void testFindAllByRankingAsc() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 10);
    // when
    List<Musical> musicals = musicalRepository.findAllByRankingAsc(pageRequest);
    // then
    assertThat(musicals)
      .as("랭킹순 정렬")
      .hasSize(2)
      .extracting(Musical::getRanking)
      .isSorted()
      .containsExactly(
        testMusical1.getRanking(),
        testMusical2.getRanking()
      );
  }

  @Test
  @DisplayName("커서 기반 페이지네이션으로 뮤지컬 목록을 조회하는 테스트")
  void testFindAllAfterCursor() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 2);
    // when
    List<Musical> musicals = musicalRepository.findAllAfterCursor(testMusical1.getId(),
      pageRequest);
    // then
    assertThat(musicals)
      .as("커서 이후 데이터")
      .hasSize(1)
      .extracting(Musical::getRanking)
      .satisfies(rankings -> {
        assertThat(rankings)
          .hasSize(1)
          .allMatch(rank -> rank > testMusical1.getRanking())
          .first()
          .isEqualTo(testMusical2.getRanking());
      });
  }

  @Test
  @DisplayName("마지막 커서 이후 데이터가 없을 경우 빈 리스트를 반환하는 테스트")
  void testFindAllAfterCursor_NoMoreData() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 2);
    // when
    List<Musical> musicals = musicalRepository.findAllAfterCursor(testMusical2.getId(),
      pageRequest);
    // then
    assertThat(musicals)
      .as("마지막 커서 이후 데이터")
      .isEmpty();
  }

}