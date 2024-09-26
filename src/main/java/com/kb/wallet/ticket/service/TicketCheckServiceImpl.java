package com.kb.wallet.ticket.service;

import static com.kb.wallet.global.common.status.ErrorCode.BAD_REQUEST_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.TICKET_STATUS_INVALID;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.exception.TicketException;
import com.kb.wallet.ticket.repository.ScheduleRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketCheckServiceImpl implements TicketCheckService{

  private final ScheduleRepository scheduleRepository;

  private final int GRADE_DIVISOR = 10;

  @Override
  public void checkTicketOwner(Ticket ticket, Member member) {
    if (!Objects.equals(ticket.getMember().getId(), member.getId())) {
      throw new TicketException(TICKET_NOT_FOUND_ERROR, "해당 티켓의 소유자가 아닙니다.");
    }
  }

  @Override
  public void checkIfTicketIsBooked(Ticket ticket) {
    if (ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new TicketException(TICKET_STATUS_INVALID, "예약된 티켓이 아닙니다.");
    }
  }

  @Override
  public void checkMusicalDate(TicketExchangeRequest exchangeRequest) {
    LocalTime localTime = convertStringToLocalTime(exchangeRequest.getPreferredSchedule());
    if (!scheduleRepository.existsByStartTime(localTime)) {
      throw new TicketException(BAD_REQUEST_ERROR, "요청 시간이 뮤지컬 예약 시작 시간과 맞지 않습니다.");
    }
  }

  @Override
  public void checkOriginalSeatGrade(Ticket ticket, TicketExchangeRequest exchangeRequest) {
    int preferredGrade = exchangeRequest.getPreferredSeatIndex() / GRADE_DIVISOR;
    if (ticket.getSeat().getSection().getGrade() != Grade.fromValue(preferredGrade)) {
      throw new TicketException(BAD_REQUEST_ERROR, "선택한 좌석 등급이 기존 티켓과 맞지 않습니다.");
    }
  }

  private LocalTime convertStringToLocalTime(String scheduleStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return LocalTime.parse(scheduleStr, formatter);
  }
}
