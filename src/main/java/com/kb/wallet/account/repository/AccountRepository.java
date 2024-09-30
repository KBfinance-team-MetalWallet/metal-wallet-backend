package com.kb.wallet.account.repository;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByMember(Member member);

    Optional<Account> findById(Long id);
}
