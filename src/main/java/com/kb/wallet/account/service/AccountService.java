package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.request.AccountRequest;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import java.util.List;

public interface AccountService {

  List<AccountResponse> getAccounts(String email);

  void createAccount(AccountRequest req, String email);

  void deleteAccount(Long id, String email);

  Account getSingleAccount(Long id);

  CursorResponse<TransactionRecordResponse> getAccountTransactionRecords(String email, Long accountId,
      Long cursor, int size);
}
