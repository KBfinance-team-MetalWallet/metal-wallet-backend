package com.kb.wallet.account.repository;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.member.domain.Member;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<List<Account>> findAllByMember(Member member);
  Optional<Account> findByMemberAndId(Member member, Long accountId);
}
