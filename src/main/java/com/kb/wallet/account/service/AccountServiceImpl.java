package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.domain.TransactionRecord;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.account.repository.AccountRepository;
import com.kb.wallet.account.repository.TransactionRecordRepository;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final TransactionRecordRepository transactionRecordRepository;
  private final PlatformTransactionManager jpaTransactionManager;

  @Override
  public Account getAccount(Long accountId) {
    return accountRepository.findById(accountId)
        .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_ERROR));
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public AccountResponse getAccount(String email, Long accountId) {
    Account account = this.getAccount(accountId);

    if (!Objects.equals(account.getMember().getEmail(), email)) {
      throw new CustomException(ErrorCode.ACCOUNT_NOT_MATCH);
    }
    return AccountResponse.toAccountResponse(account);
  }

  @Override
  public List<AccountResponse> getAccounts(String email) {
    List<Account> accounts = accountRepository.findAllByEmail(email);
    return AccountResponse.toAccountsResponseList(accounts);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public List<TransactionRecordResponse> getTransactionRecords(String email, Long accountId,
      Long cursor, int size) {
    Account account = getAccount(accountId);
    String memberEmailByAccountId = account.getMember().getEmail();

    if (!Objects.equals(memberEmailByAccountId, email)) {
      throw new CustomException(ErrorCode.ACCOUNT_NOT_MATCH);
    }

    PageRequest pageRequest = PageRequest.of(0, size, Sort.by("createdAt").descending());
    List<TransactionRecord> transactionRecords;

    transactionRecords = cursor == null ? transactionRecordRepository.findAllByAccountOrderByCreatedAtDesc(
        account, pageRequest) : transactionRecordRepository.findAllByAccountAndIdLessThanOrderByCreatedAtDesc(
        account, cursor, pageRequest);

    return transactionRecords.stream()
        .map(TransactionRecordResponse::toTransactionRecordResponse).collect(Collectors.toList());
  }
}
