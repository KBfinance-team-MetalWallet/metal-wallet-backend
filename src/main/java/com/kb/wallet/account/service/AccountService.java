package com.kb.wallet.account.service;

import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import java.util.List;

public interface AccountService {
  public List<AccountResponse> getAccounts();
  public AccountResponse getBalanceByAccountNumber(String accountNumber);
  public void createAccount(AccountRequest req);
  public void deleteAccount(Long id);
}
