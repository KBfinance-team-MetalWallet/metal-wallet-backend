package com.kb.wallet.musical.dto.response;

import com.kb.wallet.musical.domain.Musical;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MusicalDetailResponse {

  private Long id;

  private String title;

  private int ranking;

  private String place;

  private String placeDetail;

  private String ticketingStartDate;

  private String ticketingEndDate;

  private int runningTime;

  private String posterImageUrl;

  private String noticeImageUrl;

  private String detailImageUrl;

  private String placeImageUrl;

  public static MusicalDetailResponse convertToResponse(Musical musical) {
    return MusicalDetailResponse.builder()
      .id(musical.getId())
      .title(musical.getTitle())
      .ranking(musical.getRanking())
      .place(musical.getPlace())
      .placeDetail(musical.getPlaceDetail())
      .ticketingStartDate(musical.getTicketingStartDate().toString())
      .ticketingEndDate(musical.getTicketingEndDate().toString())
      .runningTime(musical.getRunningTime())
      .posterImageUrl(musical.getPosterImageUrl())
      .noticeImageUrl(musical.getNoticeImageUrl())
      .detailImageUrl(musical.getDetailImageUrl())
      .placeImageUrl(musical.getPlaceImageUrl())
      .build();
  }
}
