package com.kb.wallet.ticket.service;

import java.util.List;

public interface ScheduleService {

  List<String> getScheduleDatesByMusicalId(Long musicalId);
}