package com.kb.wallet.acount.repository;

import com.kb.wallet.acount.domain.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findByMemberId(Long memberId);
  Integer findBalanceByMemberIdAndAccountNumber(Long memberId, String AccountNumber);
}
