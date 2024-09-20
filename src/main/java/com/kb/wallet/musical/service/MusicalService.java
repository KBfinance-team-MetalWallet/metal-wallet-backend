package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.MusicalDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface MusicalService {
    // 새로운 뮤지컬 생성
    Musical saveMusical(MusicalDTO.MusicalRequest musicalRequest);

    // 모든 뮤지컬 조회
    Page<Musical> findAllMusicals(int page, int size);

    // ID로 뮤지컬 조회
    Musical findById(Long id);

    // ID로 뮤지컬 삭제
    void deleteMusical(Long id);

    // ID로 뮤지컬 제목 업데이트
    void updateTitle(Long id, String title);


}
