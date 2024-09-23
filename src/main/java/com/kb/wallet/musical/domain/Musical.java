package com.kb.wallet.musical.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "musical")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Musical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String title;

    @Column
    @NotNull
    private int rank;

    @Column
    @NotNull
    private String place;
    @Column
    @NotNull
    private String placeDetail;

    @NotNull
    private LocalDate ticketingStartDate;
    @NotNull
    private LocalDate ticketingEndDate;

    @Column
    @NotNull
    private int runningTime;
    // Todo : Url(링크) 속성도 추후에 활용해야 함. 현재 CRUD에는 필요하지 않아서 사용X.
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