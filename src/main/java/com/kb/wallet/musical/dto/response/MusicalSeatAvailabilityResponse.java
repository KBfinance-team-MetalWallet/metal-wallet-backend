package com.kb.wallet.musical.dto.response;

import com.kb.wallet.seat.dto.response.SectionAvailability;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MusicalSeatAvailabilityResponse {

  private long scheduleId;
  private String time;
  private List<String> actorNames;
  private List<SectionAvailability> sections;

  public MusicalSeatAvailabilityResponse(long scheduleId, LocalTime time) {
    this.scheduleId = scheduleId;
    this.time = time.toString();
  }
}
