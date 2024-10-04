package com.kb.wallet.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class TicketListResponse {

  private Long id;
  private String title;
  private TicketStatus ticketStatus;
  private String createdAt;
  private String validUntil;
  private String cancelUntil;
  private String place;
  private String scheduleDate;
  private String startTime;
  private String posterImageUrl;
  private Grade grade;
  private int seatNo;

  public TicketListResponse(Long id, String title, TicketStatus ticketStatus, LocalDateTime createdAt,
      LocalDateTime validUntil, LocalDateTime cancelUntil, String place,
      LocalDate scheduleDate, LocalTime startTime, String posterImageUrl,
      Grade grade, int seatNo) {
    this.id = id;
    this.title = title;
    this.ticketStatus = ticketStatus;
    this.createdAt = formatDateTime(createdAt);
    this.validUntil = formatDateTime(validUntil);
    this.cancelUntil = formatDateTime(cancelUntil);
    this.place = place;
    this.scheduleDate = formatDate(scheduleDate);
    this.startTime = formatTime(startTime);
    this.posterImageUrl = posterImageUrl;
    this.grade = grade;
    this.seatNo = seatNo;
  }

  // LocalDateTime을 "년.월.일(요일) 시:분" 형태로 변환하는 메서드
  private String formatDateTime(LocalDateTime dateTime) {
    if (dateTime == null) return null;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E) HH:mm", Locale.KOREAN);
    return dateTime.format(dateTimeFormatter);
  }

  // LocalDate를 "년.월.일(요일)" 형태로 변환하는 메서드
  private String formatDate(LocalDate date) {
    if (date == null) return null;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN);
    return date.format(dateFormatter);
  }

  // LocalTime을 "시:분" 형태로 변환하는 메서드
  private String formatTime(LocalTime time) {
    if (time == null) return null;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    return time.format(timeFormatter);
  }
}