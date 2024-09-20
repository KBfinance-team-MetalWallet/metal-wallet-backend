package com.kb.wallet.musical.dto;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MusicalDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class MusicalRequest {
        private Long id;
        private String title;
        private int rank;
        private String place;
        private String placeDetail;
        private LocalDate  ticketingStartDate;
        private LocalDate ticketingEndDate;
        private int runningTime;
//        private String noticeImageUrl;
//        private String posterImageUrl;
//        private String detailImageUrl;
//        private String placeImageUrl;
    }
    public static Musical toMusical(MusicalRequest musicalRequest){
        return Musical.builder()
            .id(musicalRequest.getId())
            .title(musicalRequest.getTitle())
            .rank(musicalRequest.getRank())
            .place(musicalRequest.getPlace())
            .placeDetail(musicalRequest.getPlaceDetail())
            .ticketingStartDate(musicalRequest.getTicketingStartDate())
            .ticketingEndDate(musicalRequest.getTicketingEndDate())
            .runningTime(musicalRequest.getRunningTime())
            .build();
    }


}
