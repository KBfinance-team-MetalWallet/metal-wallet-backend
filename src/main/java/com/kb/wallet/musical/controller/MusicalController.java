package com.kb.wallet.musical.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.response.MusicalDetailResponse;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.service.MusicalService;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Builder
@RestController
@Slf4j
@RequestMapping("/musicals")
public class MusicalController {

  private final MusicalService musicalService;

  @GetMapping("/{musicalId}")
  public ApiResponse<MusicalDetailResponse> getMusicalById(
    @PathVariable(name = "musicalId") Long musicalId) {
    Musical musical = musicalService.getMusicalById(musicalId);
    return ApiResponse.ok(MusicalDetailResponse.convertToResponse(musical));
  }

  @GetMapping
  public ApiResponse<CursorResponse<MusicalResponse>> getMusicals(
    @RequestParam(name = "cursor", required = false) Long cursor,
    @RequestParam(name = "size", defaultValue = "10") int size) {

    List<MusicalResponse> musicals;
    Long nextCursor = null;

    // 커서가 없는 경우 처음 요소를 가져오고, 있는 경우 커서 기준으로 데이터를 가져옴
    if (cursor == null) {
      musicals = musicalService.getMusicalsWithLimit(size);
    } else {
      musicals = musicalService.getMusicalsAfterCursor(cursor, size);
    }

    // 마지막 요소 기반으로 다음 커서를 결정
    if (!musicals.isEmpty()) {
      nextCursor = musicals.get(musicals.size() - 1).getId(); // getId() 메서드를 통해 커서 값 반환
    }

    CursorResponse<MusicalResponse> cursorResponse = new CursorResponse<>(musicals, nextCursor);
    return ApiResponse.ok(cursorResponse);
  }

  @GetMapping("/{musicalId}/seats-availability")
  public ApiResponse<List<MusicalSeatAvailabilityResponse>> getScheduleInfos(
    @AuthenticationPrincipal Member member, @PathVariable(name = "musicalId") Long musicalId,
    @RequestParam("date") String date) {

    List<MusicalSeatAvailabilityResponse> responses = musicalService.getScheduleInfos(
      musicalId, date);

    return ApiResponse.ok(responses);
  }

  @GetMapping("/{musicalId}/dates")
  public ApiResponse<MusicalScheduleResponse> getScheduleDates(
    @AuthenticationPrincipal Member member, @PathVariable(name = "musicalId") Long musicalId) {

    List<String> dates = musicalService.getScheduleDates(musicalId).stream().toList();

    MusicalScheduleResponse response = MusicalScheduleResponse.builder().musicalId(musicalId)
      .scheduleDate(dates).build();
    return ApiResponse.ok(response);
  }

  @GetMapping("/schedules/{scheduleId}/seats")
  public ApiResponse<List<MusicalScheduleSeatAvailabilityResponse>> getAvailableSeats(
    @AuthenticationPrincipal Member member, @PathVariable(name = "scheduleId") Long scheduleId) {

    List<MusicalScheduleSeatAvailabilityResponse> responses = musicalService.getAvailableSeats(
      scheduleId);

    return ApiResponse.ok(responses);
  }

}
