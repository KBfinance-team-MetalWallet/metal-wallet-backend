package com.kb.wallet.musical.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicalScheduleSeatAvailabilityResponse {

  private List<Long> availableSeats;
}


