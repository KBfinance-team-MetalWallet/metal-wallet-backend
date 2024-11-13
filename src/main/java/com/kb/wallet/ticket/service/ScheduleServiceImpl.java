package com.kb.wallet.ticket.service;

import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  @Override
  public Set<String> getScheduleDatesByMusicalId(Long musicalId) {
    return scheduleRepository.findByMusicalId(musicalId).stream()
        .map(schedule -> schedule.getDate().toString())
        .collect(Collectors.toSet());
  }
}