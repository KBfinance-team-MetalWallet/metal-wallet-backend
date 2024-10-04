package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalInfoUpdateResponse;
import com.kb.wallet.musical.repository.MusicalRepository;
import com.kb.wallet.ticket.service.ScheduleService;
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

    @Autowired
    public MusicalServiceImpl(MusicalRepository musicalRepository) {
        this.musicalRepository = musicalRepository;
    }

    @Override
    @Transactional("jpaTransactionManager")
    public Musical saveMusical(MusicalCreationRequest request) {
        Musical musical = MusicalCreationRequest.toMusical(request);
        return musicalRepository.save(musical);
    }

    @Override
    public Page<Musical> findAllMusicals(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return musicalRepository.findAll(pageable);
    }

    @Override
    public Musical findById(Long id) {
        return musicalRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional("jpaTransactionManager")
    public void deleteMusical(Long id) {
        Musical musical = musicalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Musical not found"));
        musicalRepository.delete(musical);
    }


    @Override
    @Transactional("jpaTransactionManager")
    public MusicalInfoUpdateResponse updateMusicalInfo(Long id, MusicalInfoUpdateRequest request) {
        Musical musical = musicalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Musical not found"));
        musical.setTitle(request.getTitle());
        musical.setRanking(request.getRanking());
        musical.setPlace(request.getPlace());
        musical.setPlaceDetail(request.getPlaceDetail());
        musical.setTicketingStartDate(request.getTicketingStartDate());
        musical.setTicketingEndDate(request.getTicketingEndDate());
        musical.setRunningTime(request.getRunningTime());

        musicalRepository.save(musical);
        return MusicalInfoUpdateResponse.toMusicalInfoUpdateResponse(musical);

  }

  @Override
  public List<LocalDate> getScheduleDates(Long musicalId) {
    return scheduleService.getScheduleDatesByMusicalId(musicalId);
  }
}


