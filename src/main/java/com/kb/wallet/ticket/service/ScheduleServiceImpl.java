package com.kb.wallet.ticket.service;

import com.kb.wallet.ticket.domain.Schedule;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  @Override
  public List<LocalDate> getScheduleDatesByMusicalId(Long musicalId) {
    return scheduleRepository.findByMusicalId(musicalId)
        .stream()
        .map(Schedule::getDate)
        .collect(Collectors.toList());
  }
}