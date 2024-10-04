package com.kb.wallet.musical.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicalScheduleResponse {

  private Long musicalId;
  private List<LocalDate> scheduleDate;
}