package com.kb.wallet.ticket.service;

import java.util.Set;

public interface ScheduleService {

  Set<String> getScheduleDatesByMusicalId(Long musicalId);
}