package com.kb.wallet.ticket.repository;

import com.kb.wallet.ticket.domain.Schedule;
import java.time.LocalTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  @Query("SELECT s FROM Schedule s JOIN FETCH s.musical WHERE s.musical.id = :musicalId")
  List<Schedule> findByMusicalId(@Param("musicalId") Long musicalId);

}
