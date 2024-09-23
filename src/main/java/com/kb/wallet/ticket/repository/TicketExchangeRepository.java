package com.kb.wallet.ticket.repository;

import com.kb.wallet.ticket.domain.TicketExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketExchangeRepository extends JpaRepository<TicketExchange, Long> {

}
