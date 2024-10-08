package com.kb.wallet.seat.repository;

import com.kb.wallet.seat.domain.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

  @Query("SELECT s FROM Seat s WHERE s.schedule.id = :scheduleId AND s.isAvailable = true")
  List<Seat> findAvailableSeatsByScheduleId(@Param("scheduleId") Long scheduleId);
}
