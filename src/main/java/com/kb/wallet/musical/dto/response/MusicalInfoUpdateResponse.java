package com.kb.wallet.musical.dto.response;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicalInfoUpdateResponse {
    private Long id;
    private String title;
    private int ranking;
    private String place;
    private String placeDetail;
    private LocalDate ticketingStartDate;
    private LocalDate ticketingEndDate;
    private int runningTime;

    // Todo : Url(링크) 속성도 추후에 활용해야 함. 현재 CRUD에는 필요하지 않아서 사용X.
//        private String noticeImageUrl;
//        private String posterImageUrl;
//        private String detailImageUrl;
//        private String placeImageUrl;

    public static MusicalInfoUpdateResponse toMusicalInfoUpdateResponse(Musical musical){
        return MusicalInfoUpdateResponse.builder()
            .id(musical.getId())
            .title(musical.getTitle())
            .ranking(musical.getRanking())
            .place(musical.getPlace())
            .placeDetail(musical.getPlaceDetail())
            .ticketingStartDate(musical.getTicketingStartDate())
            .ticketingEndDate(musical.getTicketingEndDate())
            .runningTime(musical.getRunningTime())
            .build();
    }
}
