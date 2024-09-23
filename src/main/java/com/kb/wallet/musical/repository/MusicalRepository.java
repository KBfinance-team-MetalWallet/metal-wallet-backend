package com.kb.wallet.musical.repository;

import com.kb.wallet.musical.domain.Musical;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicalRepository extends JpaRepository<Musical, Long> {
    Page<Musical> findAll(Pageable pageable);

    @Override
    Optional<Musical> findById(Long musicalId);





}

