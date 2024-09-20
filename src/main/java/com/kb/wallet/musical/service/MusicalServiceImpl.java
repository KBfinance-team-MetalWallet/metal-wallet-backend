package com.kb.wallet.musical.service;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.MusicalDTO;
import com.kb.wallet.musical.repository.MusicalRepository;
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
    public Musical saveMusical(MusicalDTO.MusicalRequest musicalRequest) {
        Musical musical = MusicalDTO.toMusical(musicalRequest);
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
    public void updateTitle(Long id, String title) {
        Musical musical = musicalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Musical not found"));
        musical.setTitle(title);
        musicalRepository.save(musical);
    }
}


