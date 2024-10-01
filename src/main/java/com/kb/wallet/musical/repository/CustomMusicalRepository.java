package com.kb.wallet.musical.repository;

import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import java.time.LocalDate;
import java.util.List;

public interface CustomMusicalRepository {

    List<MusicalSeatAvailabilityResponse> findMusicalSeatAvailability(Long musicalId,
            LocalDate specificDate);
}
