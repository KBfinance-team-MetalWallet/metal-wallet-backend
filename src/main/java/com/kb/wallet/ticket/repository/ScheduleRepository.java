package com.kb.wallet.ticket.repository;

import com.kb.wallet.ticket.domain.Schedule;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  List<Schedule> findByMusicalId(Long musicalId);

  boolean existsByStartTime(LocalTime startTime);
}
