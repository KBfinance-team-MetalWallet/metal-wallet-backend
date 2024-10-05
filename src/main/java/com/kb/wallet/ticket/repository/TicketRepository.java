package com.kb.wallet.ticket.repository;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

  Optional<Ticket> findByIdAndMember(Long ticketId, Member member);

  @Query("SELECT t FROM Ticket t "
      + "WHERE t.id = :id "
      + "AND t.member.email = :email ")
  Optional<Ticket> findByMember(
      @Param("id") Long id,
      @Param("email") String email);

  @Query("SELECT t FROM Ticket t WHERE t.member.email = :email AND t.ticketStatus = :status")
  Page<Ticket> findTicketsByMemberAndTicketStatus(
      @Param("email") String email,
      @Param("status") TicketStatus status,
      Pageable pageable);

  Optional<Ticket> findById(Long ticketId);
}