package com.kb.wallet.musical.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.repository.CustomMusicalRepository;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.domain.Seat;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.service.ScheduleService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MusicalServiceImpl implements MusicalService {

  private final MusicalRepository musicalRepository;
  private final CustomMusicalRepository customMusicalRepository;
  private final ScheduleService scheduleService;
  private final SeatRepository seatRepository;

  @Override
  public Musical getMusicalById(Long musicalId) {
    return musicalRepository.findById(musicalId)
      .orElseThrow(() -> new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
  }

  @Override
  public List<MusicalResponse> getMusicalsWithLimit(int size) {
    List<Musical> musicals = musicalRepository.findAllByRankingAsc(PageRequest.of(0, size));
    return musicals.stream().map(MusicalResponse::convertToResponse).toList();
  }

  @Override
  public List<MusicalResponse> getMusicalsAfterCursor(Long cursor, int size) {
    List<Musical> musicals = musicalRepository.findAllAfterCursor(cursor, PageRequest.of(0, size));
    return musicals.stream().map(MusicalResponse::convertToResponse).toList();
  }

  @Override
  public List<MusicalSeatAvailabilityResponse> getScheduleInfos(Long musicalId, String date) {
    LocalDate localDate = LocalDate.parse(date);
    return customMusicalRepository.findMusicalSeatAvailability(musicalId, localDate);
  }

  @Override
  public Set<String> getScheduleDates(Long musicalId) {
    return scheduleService.getScheduleDatesByMusicalId(musicalId);
  }

  @Override
  public List<MusicalScheduleSeatAvailabilityResponse> getAvailableSeats(
    Long scheduleId) {
    List<Seat> seatList = seatRepository.findAvailableSeatsByScheduleId(scheduleId);
    return seatList.stream().map(MusicalScheduleSeatAvailabilityResponse::new).toList();
  }
}