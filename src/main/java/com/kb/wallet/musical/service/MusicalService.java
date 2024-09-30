package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalInfoUpdateResponse;
import org.springframework.data.domain.Page;

public interface MusicalService {

  // 새로운 뮤지컬 생성
  Musical saveMusical(MusicalCreationRequest request);

  // 모든 뮤지컬 조회
  Page<Musical> findAllMusicals(int page, int size);

  // ID로 뮤지컬 조회
  Musical findById(Long musicalId);

  // ID로 뮤지컬 삭제
  void deleteMusical(Long musicalId);

  // ID로 뮤지컬 제목 업데이트
  MusicalInfoUpdateResponse updateMusicalInfo(Long musicalId, MusicalInfoUpdateRequest request);


}
