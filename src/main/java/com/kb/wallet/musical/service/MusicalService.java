package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import java.util.List;
import java.util.Set;

public interface MusicalService {

  Musical getMusicalById(Long id);

  List<MusicalResponse> getMusicalsWithLimit(int size);

  List<MusicalResponse> getMusicalsAfterCursor(Long cursor, int size);

  List<MusicalSeatAvailabilityResponse> getScheduleInfos(Long id, String date);

  Set<String> getScheduleDates(Long musicalId);

  List<MusicalScheduleSeatAvailabilityResponse> getAvailableSeats(Long scheduleId);
}
