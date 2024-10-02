package com.kb.wallet.musical.repository;

import com.kb.wallet.musical.domain.Musical;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicalRepository extends JpaRepository<Musical, Long> {

  Page<Musical> findAll(Pageable pageable);

  Optional<Musical> findById(Long musicalId);

  @Query("SELECT m FROM Musical m ORDER BY m.ranking ASC")
  List<Musical> findAllByRankingAsc(Pageable pageable);

  @Query("SELECT m FROM Musical m WHERE m.id > :cursor ORDER BY m.id ASC")
  List<Musical> findAllAfterCursor(@Param("cursor") Long cursor, Pageable pageable);
}

