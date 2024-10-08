package com.kb.wallet.ticket.service;

import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  @Override
  public Set<String> getScheduleDatesByMusicalId(Long musicalId) {
    return scheduleRepository.findByMusicalId(musicalId).stream()
      .map(schedule -> schedule.getDate().toString())
      .collect(Collectors.toSet());
  }
}