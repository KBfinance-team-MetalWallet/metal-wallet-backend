package com.kb.wallet.musical.dto.request;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicalCreationRequest {

  @NotBlank(message = "뮤지컬 제목은 필수 입력 사항입니다.")
  private String title;

  @NotBlank(message = "뮤지컬 상영 장소는 필수 입력 사항입니다.")
  private String place;

  private String placeDetail;

  @NotNull(message = "뮤지컬 상영 시작 시간을 필수 입력 사항입니다.")
  private LocalDate ticketingStartDate;

  @NotNull(message = "뮤지컬 상영 종료 시간을 필수 입력 사항입니다.")
  private LocalDate ticketingEndDate;

  @NotNull(message = "뮤지컬 상영 시간은 필수 입력 사항입니다.")
  private Integer runningTime;

  public static Musical toMusical(MusicalCreationRequest request) {
    return Musical.builder()
        .title(request.getTitle())
        .place(request.getPlace())
        .placeDetail(request.getPlaceDetail())
        .ticketingStartDate(request.getTicketingStartDate())
        .ticketingEndDate(request.getTicketingEndDate())
        .runningTime(request.getRunningTime())
        .build();
  }
}
