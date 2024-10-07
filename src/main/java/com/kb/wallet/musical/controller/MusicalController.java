package com.kb.wallet.musical.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalCreationResponse;
import com.kb.wallet.musical.dto.response.MusicalDetailResponse;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleResponse;
import com.kb.wallet.musical.dto.response.MusicalScheduleSeatAvailabilityResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.service.MusicalService;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@Slf4j
@RequestMapping("/musicals")
public class MusicalController {

  private final MusicalService musicalService;

  @Autowired
  public MusicalController(MusicalService musicalService) {
    this.musicalService = musicalService;
  }

  @GetMapping
  public ApiResponse<CursorResponse<MusicalResponse>> findAll(
    @RequestParam(name = "cursor", required = false) Long cursor,
    @RequestParam(name = "size", defaultValue = "10") int size) {

    List<MusicalResponse> musicals;
    Long nextCursor = null;

    // 커서가 없는 경우 처음 요소를 가져오고, 있는 경우 커서 기준으로 데이터를 가져옴
    if (cursor == null) {
      musicals = musicalService.findAllMusicals(size);
    } else {
      musicals = musicalService.findMusicalsAfterCursor(cursor, size);
    }

    // 마지막 요소 기반으로 다음 커서를 결정
    if (!musicals.isEmpty()) {
      nextCursor = musicals.get(musicals.size() - 1).getId(); // getId() 메서드를 통해 커서 값 반환
    }

    CursorResponse<MusicalResponse> cursorResponse = new CursorResponse<>(musicals, nextCursor);
    return ApiResponse.ok(cursorResponse);
  }

  @GetMapping("/{musicalId}")
  public ApiResponse<MusicalDetailResponse> findById(
    @PathVariable(name = "musicalId") Long musicalId) {
    Musical musical = musicalService.findById(musicalId);
    return ApiResponse.ok(MusicalDetailResponse.convertToResponse(musical));
  }

  @PostMapping
  public ResponseEntity<MusicalCreationResponse> createMusical(
    @RequestBody @Valid MusicalCreationRequest request) {
    MusicalCreationResponse savedMusical = musicalService.saveMusical(request);
    return ResponseEntity.ok(savedMusical);
  }

  @DeleteMapping("/{musicalId}")
//  @PreAuthorize("hasRole('ADMIN')") // 관리자만 뮤지컬 정보 삭제 가능
  public ResponseEntity<Void> delete(@PathVariable(name = "musicalId") Long musicalId) {
    musicalService.deleteMusical(musicalId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{musicalId}")
//  @PreAuthorize("hasRole('ADMIN')") // 관리자만 뮤지컬 정보 업데이트 가능
  public ResponseEntity<Void> updateMusicalInfo(
    @PathVariable(name = "musicalId") Long musicalId,
    @RequestBody MusicalInfoUpdateRequest request) {
    /**
     * TODO : Login Authentication 추가 예정
     */
    musicalService.updateMusicalInfo(musicalId, request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{musicalId}/seats-availability")
  public ApiResponse<List<MusicalSeatAvailabilityResponse>> checkSeatAvailability(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "musicalId") Long musicalId,
    @RequestParam("date") String date) {

    List<MusicalSeatAvailabilityResponse> responses = musicalService.checkSeatAvailability(
      musicalId,
      date);

    return ApiResponse.ok(responses);
  }

  @GetMapping("/{musicalId}/dates")
  public ApiResponse<MusicalScheduleResponse> getScheduleDates(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "musicalId") Long musicalId) {

    List<String> dates = musicalService.getScheduleDates(musicalId).stream().toList();

    MusicalScheduleResponse response = MusicalScheduleResponse.builder()
      .musicalId(musicalId)
      .scheduleDate(dates)
      .build();
    return ApiResponse.ok(response);
  }

  @GetMapping("/schedules/{scheduleId}/seats")
  public ApiResponse<List<MusicalScheduleSeatAvailabilityResponse>> getScheduleSeatAvailability(
      @AuthenticationPrincipal Member member,
      @PathVariable(name = "scheduleId") Long scheduleId) {

    List<Long> availableSeats = musicalService.getAvailableSeatsByScheduleId(scheduleId);

    MusicalScheduleSeatAvailabilityResponse response = MusicalScheduleSeatAvailabilityResponse.builder()
        .availableSeats(availableSeats)
        .build();

    return ApiResponse.ok(Collections.singletonList(response));
  }

}
