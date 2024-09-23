package com.kb.wallet.musical.dto.request;

import com.kb.wallet.musical.domain.Musical;
import java.time.LocalDate;
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
    private Long id;
    private String title;
    private int rank;
    private String place;
    private String placeDetail;
    private LocalDate ticketingStartDate;
    private LocalDate ticketingEndDate;
    private int runningTime;

    public static Musical toMusical(MusicalCreationRequest request){
        return Musical.builder()
            .title(request.getTitle())
            .rank(request.getRank())
            .place(request.getPlace())
            .placeDetail(request.getPlaceDetail())
            .ticketingStartDate(request.getTicketingStartDate())
            .ticketingEndDate(request.getTicketingEndDate())
            .runningTime(request.getRunningTime())
            .build();
    }
}
