package com.kb.wallet.seat.dto.response;

import com.kb.wallet.seat.constant.Grade;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SectionAvailability {

    private Grade section;
    private int availableSeats;

    public SectionAvailability(Grade grade, int availableSeats) {
        this.section = grade;
        this.availableSeats = availableSeats;
    }
}
