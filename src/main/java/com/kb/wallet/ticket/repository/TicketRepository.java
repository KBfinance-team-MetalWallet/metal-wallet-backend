package com.kb.wallet.ticket.repository;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

  @Query("SELECT t FROM Ticket t "
      + "WHERE t.id = :id "
      + "AND t.member.email = :email ")
  Optional<Ticket> findByMember(
      @Param("id") Long id,
      @Param("email") String email);

  @Query("SELECT new com.kb.wallet.ticket.dto.response.TicketListResponse(" +
      "t.id, m.title, t.ticketStatus, " +
      "t.createdAt, t.validUntil, t.cancelUntil, " +
      "m.place, sc.date, sc.startTime, " +
      "m.posterImageUrl, sec.grade, s.seatNo) " +
      "FROM Ticket t " +
      "JOIN t.musical m " +
      "JOIN t.seat s " +
      "JOIN s.section sec " +
      "JOIN s.schedule sc " +
      "WHERE t.member.email = :email " +
      "AND (:status IS NULL OR t.ticketStatus = :status) " +
      "AND (:cursor IS NULL OR t.id < :cursor) " +
      "ORDER BY t.id desc")
  List<TicketListResponse> findAllByMemberAndTicketStatus(
      @Param("email") String email,
      @Param("status") TicketStatus status,
      @Param("cursor") Long cursor,
      Pageable pageable);

  Optional<Ticket> findById(Long ticketId);
}