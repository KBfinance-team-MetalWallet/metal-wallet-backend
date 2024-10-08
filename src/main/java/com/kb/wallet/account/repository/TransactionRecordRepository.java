package com.kb.wallet.account.repository;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.domain.TransactionRecord;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
  List<TransactionRecord> findAllByAccountAndIdLessThanOrderByCreatedAtDesc(
      Account account, Long cursor, Pageable pageable);
  List<TransactionRecord> findAllByAccountOrderByCreatedAtDesc(Account account, Pageable pageable);

}
