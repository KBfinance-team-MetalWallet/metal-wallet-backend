package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalCreationResponse;
import com.kb.wallet.musical.dto.response.MusicalInfoUpdateResponse;
import com.kb.wallet.musical.dto.response.MusicalResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface MusicalService {

  // 새로운 뮤지컬 생성
  MusicalCreationResponse saveMusical(MusicalCreationRequest request);

  // 모든 뮤지컬 조회
  Page<MusicalResponse> findAllMusicals(int page, int size);

  // ID로 뮤지컬 조회
  Musical findById(Long musicalId);

  // ID로 뮤지컬 삭제
  void deleteMusical(Long musicalId);

  // ID로 뮤지컬 제목 업데이트
  MusicalInfoUpdateResponse updateMusicalInfo(Long musicalId, MusicalInfoUpdateRequest request);

  // 뮤지컬 일정마다 좌석정보 확인
  List<MusicalSeatAvailabilityResponse> checkSeatAvailability(Long id, String date);

  // 모든 뮤지컬 조회 (커서 페이징, 처음 데이터를 가져올 때)
  List<MusicalResponse> findAllMusicals(int size);

  // 모든 뮤지컬 조회 (커서 페이징, 커서 이후의 데이터를 가져올 때)
  List<MusicalResponse> findMusicalsAfterCursor(Long cursor, int size);
}
