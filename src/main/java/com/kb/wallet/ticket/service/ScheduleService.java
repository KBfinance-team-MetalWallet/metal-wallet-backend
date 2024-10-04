package com.kb.wallet.ticket.service;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

  List<LocalDate> getScheduleDatesByMusicalId(Long musicalId);
}