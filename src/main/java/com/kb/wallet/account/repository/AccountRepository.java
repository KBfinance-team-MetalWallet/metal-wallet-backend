package com.kb.wallet.account.repository;

import com.kb.wallet.account.domain.Account;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a JOIN FETCH a.member m WHERE m.email = :email")
    List<Account> findAllByMember(@Param("email") String email);

    Optional<Account> findById(Long id);
}
