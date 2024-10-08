package com.kb.wallet.musical.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicalScheduleResponse {

  private Long musicalId;
  private List<String> scheduleDate;
}