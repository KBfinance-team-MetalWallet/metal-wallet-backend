package com.kb.wallet.musical.dto.response;

import com.kb.wallet.musical.domain.Musical;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MusicalResponse {

  private Long id;
  private String title;
  private int ranking;
  private String place;
  private String placeDetail;
  private String ticketingStartDate;
  private String ticketingEndDate;
  private String posterImageUrl;

  public static MusicalResponse convertToResponse(Musical musical) {
    return MusicalResponse.builder()
      .id(musical.getId())
      .title(musical.getTitle())
      .ranking(musical.getRanking())
      .place(musical.getPlace())
      .placeDetail(musical.getPlaceDetail())
      .ticketingStartDate(musical.getTicketingStartDate().toString())
      .ticketingEndDate(musical.getTicketingEndDate().toString())
      .posterImageUrl(musical.getPosterImageUrl())
      .build();
  }
}
