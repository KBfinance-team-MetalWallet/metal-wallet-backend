package com.kb.wallet.seat.repository;

import com.kb.wallet.seat.domain.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

  @Query("SELECT s.id FROM Seat s WHERE s.schedule.id = :scheduleId AND s.isAvailable = true")
  List<Long> findAvailableSeatsByScheduleId(@Param("scheduleId") Long scheduleId);
}
