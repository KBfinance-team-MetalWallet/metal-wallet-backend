package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import java.util.List;

public interface AccountService {

  Account getAccount(Long id);

  AccountResponse getAccount(String email, Long accountId);

  List<AccountResponse> getAccounts(String email);

  CursorResponse<TransactionRecordResponse> getAccountTransactionRecords(String email,
    Long accountId,
    Long cursor, int size);
}
