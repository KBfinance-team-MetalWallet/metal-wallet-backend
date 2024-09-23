package com.kb.wallet.musical.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicalInfoUpdateRequest {
    private Long id;
    private String title;
    private int rank;
    private String place;
    private String placeDetail;
    private LocalDate ticketingStartDate;
    private LocalDate ticketingEndDate;
    private int runningTime;
}