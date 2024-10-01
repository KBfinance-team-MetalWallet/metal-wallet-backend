package com.kb.wallet.ticket.repository;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.domain.TicketExchange;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketExchangeRepository extends JpaRepository<TicketExchange, Long> {
  @Query("SELECT te FROM TicketExchange te WHERE te.ticket.member = :member")
  Page<TicketExchange> findByTicketMember(@Param("member") Member member, Pageable pageable);

  Optional<TicketExchange> findByTicketId(Long ticketId);
}
