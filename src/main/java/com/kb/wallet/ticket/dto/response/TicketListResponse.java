package com.kb.wallet.ticket.dto.response;

import com.kb.wallet.seat.constant.Grade;
import com.kb.wallet.ticket.constant.TicketStatus;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
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

  public TicketListResponse(Long id, String title, TicketStatus ticketStatus,
      LocalDateTime createdAt, LocalDateTime validUntil, LocalDateTime cancelUntil,
      String place, LocalDate scheduleDate, LocalTime startTime,
      String posterImageUrl, Grade grade, int seatNo) {
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


  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy.MM.dd(E) HH:mm", Locale.KOREAN);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy.MM.dd(E)", Locale.KOREAN);
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "HH:mm", Locale.KOREAN);



  // LocalDateTime을 "년.월.일(요일) 시:분" 형태로 변환하는 메서드
  public static String formatDateTime(LocalDateTime dateTime) {
    return (dateTime == null) ? null : dateTime.format(DATE_TIME_FORMATTER);
  }

  // LocalDate를 "년.월.일(요일)" 형태로 변환하는 메서드
  public static String formatDate(LocalDate date) {
    return (date == null) ? null : date.format(DATE_FORMATTER);
  }

  // LocalTime을 "시:분" 형태로 변환하는 메서드
  public static String formatTime(LocalTime time) {
    return (time == null) ? null : time.format(TIME_FORMATTER);
  }

}