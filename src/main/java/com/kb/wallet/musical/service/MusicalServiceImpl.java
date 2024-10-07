package com.kb.wallet.musical.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalCreationResponse;
import com.kb.wallet.musical.dto.response.MusicalInfoUpdateResponse;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.repository.CustomMusicalRepository;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.seat.repository.SeatRepository;
import com.kb.wallet.ticket.service.ScheduleService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MusicalServiceImpl implements MusicalService {

  private final MusicalRepository musicalRepository;
  private final CustomMusicalRepository customMusicalRepository;
  private final ScheduleService scheduleService;
  private final SeatRepository seatRepository;

  @Autowired
  public MusicalServiceImpl(MusicalRepository musicalRepository,
    CustomMusicalRepository customMusicalRepository,
    ScheduleService scheduleService, SeatRepository seatRepository) {
    this.musicalRepository = musicalRepository;
    this.customMusicalRepository = customMusicalRepository;
    this.scheduleService = scheduleService;
    this.seatRepository = seatRepository;
  }

  @Override
  @Transactional("jpaTransactionManager")
  public MusicalCreationResponse saveMusical(MusicalCreationRequest request) {
    Musical musical = MusicalCreationRequest.toMusical(request);
    Musical saved = musicalRepository.save(musical);
    return MusicalCreationResponse.toMusical(saved);
  }

  @Override
  public Musical findById(Long musicalId) {
    return musicalRepository.findById(musicalId)
      .orElseThrow(() -> new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
  }

  @Override
  @Transactional("jpaTransactionManager")
  public void deleteMusical(Long musicalId) {
    Musical musical = musicalRepository.findById(musicalId)
      .orElseThrow(() -> new CustomException(ErrorCode.MUSICAL_NOT_FOUND,
        "요청한 뮤지컬을 찾을 수 없습니다."));
    musicalRepository.delete(musical);
  }

  @Override
  @Transactional("jpaTransactionManager")
  public MusicalInfoUpdateResponse updateMusicalInfo(Long musicalId,
    MusicalInfoUpdateRequest request) {
    Musical musical = musicalRepository.findById(musicalId)
      .orElseThrow(() -> new CustomException(ErrorCode.MUSICAL_NOT_FOUND,
        "요청한 뮤지컬을 찾을 수 없습니다."));

    try {
      Musical updatedMusical = Musical.builder()
        .id(musical.getId())
        .title(request.getTitle())
        .ranking(request.getRanking())
        .place(request.getPlace())
        .placeDetail(request.getPlaceDetail())
        .ticketingStartDate(request.getTicketingStartDate())
        .ticketingEndDate(request.getTicketingEndDate())
        .runningTime(request.getRunningTime())
        .build();

      Musical savedMusical = musicalRepository.save(updatedMusical);
      return MusicalInfoUpdateResponse.toMusicalInfoUpdateResponse(savedMusical);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.ENCRYPTION_ERROR, "Musical 정보 업데이트 중 오류가 발생했습니다.");
    }
  }

  @Override
  public List<MusicalSeatAvailabilityResponse> checkSeatAvailability(Long id, String date) {
    LocalDate localDate = LocalDate.parse(date);
    return customMusicalRepository.findMusicalSeatAvailability(id, localDate);
  }

  @Override
  public List<MusicalResponse> findAllMusicals(int size) {
    List<Musical> musicals = musicalRepository.findAllByRankingAsc(PageRequest.of(0, size));
    return musicals.stream()
      .map(MusicalResponse::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  public List<MusicalResponse> findMusicalsAfterCursor(Long cursor, int size) {
    List<Musical> musicals = musicalRepository.findAllAfterCursor(cursor,
      PageRequest.of(0, size));
    return musicals.stream()
      .map(MusicalResponse::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  public Set<String> getScheduleDates(Long musicalId) {
    return scheduleService.getScheduleDatesByMusicalId(musicalId);
  }

  @Override
  public List<Long> getAvailableSeatsByScheduleId(Long scheduleId) {
     return seatRepository.findAvailableSeatsByScheduleId(scheduleId);
  }
}