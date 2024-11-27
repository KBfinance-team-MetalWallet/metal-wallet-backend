package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import java.util.List;

public interface AccountService {

  Account getAccountById(Long id);

  AccountResponse getAccountById(String email, Long accountId);

  List<AccountResponse> getAccountsByEmail(String email);

  List<TransactionRecordResponse> getTransactionRecords(String email, Long accountId,
      Long cursor, int size);

}
