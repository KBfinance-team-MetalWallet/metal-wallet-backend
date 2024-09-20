package com.kb.wallet.musical.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "musical")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Musical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String title;

    @Column(name = "`rank`")
    @NotNull
    private int rank;

    @Column
    @NotNull
    private String place;
    @Column
    @NotNull
    private String placeDetail;

    @Column
    @NotNull
    private LocalDate ticketingStartDate;

    @Column
    @NotNull
    private LocalDate ticketingEndDate;

    @Column
    @NotNull
    private int runningTime;

//    @Column
//    @NotNull
//    private String noticeImageUrl;
//    @Column
//    @NotNull
//    private String posterImageUrl;
//    @Column
//    @NotNull
//    private String detailImageUrl;
//    @Column
//    @NotNull
//    private String placeImageUrl;


    public Musical(Long id, String title, int rank, String place, String placeDetail) {
        this.id = id;
        this.title = title;
        this.rank = rank;
        this.place = place;
        this.placeDetail = placeDetail;
        this.ticketingStartDate = ticketingStartDate;
        this.ticketingEndDate = ticketingEndDate;
        this.runningTime = runningTime;
    }
}