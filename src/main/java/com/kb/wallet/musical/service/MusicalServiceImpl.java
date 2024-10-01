package com.kb.wallet.musical.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalInfoUpdateResponse;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.repository.CustomMusicalRepository;
import com.kb.wallet.musical.repository.MusicalRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MusicalServiceImpl implements MusicalService {

    private final MusicalRepository musicalRepository;
    private final CustomMusicalRepository customMusicalRepository;

    @Autowired
    public MusicalServiceImpl(MusicalRepository musicalRepository,
            CustomMusicalRepository customMusicalRepository) {
        this.musicalRepository = musicalRepository;
        this.customMusicalRepository = customMusicalRepository;
    }

    @Override
    @Transactional("jpaTransactionManager")
    public Musical saveMusical(MusicalCreationRequest request) {
        Musical musical = MusicalCreationRequest.toMusical(request);
        return musicalRepository.save(musical);
    }

    @Override
    public Page<Musical> findAllMusicals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return musicalRepository.findAll(pageable);
    }

    @Override
    public Musical findById(Long musicalId) {
        return musicalRepository.findById(musicalId).orElse(null);
    }

    @Override
    @Transactional("jpaTransactionManager")
    public void deleteMusical(Long musicalId) {
        try {
            Musical musical = musicalRepository.findById(musicalId)
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.MUSICAL_NOT_FOUND,
                                    "요청한 뮤지컬을 찾을 수 없습니다."));
            musicalRepository.delete(musical);
        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "뮤지컬 삭제 중 오류가 발생했습니다.");
        }
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
}


