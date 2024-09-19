package com.kb.wallet.acount.service;

import com.kb.wallet.acount.dto.request.AccountRequest;
import com.kb.wallet.acount.dto.response.AccountResponse;
import java.util.List;

public interface AccountService {
  public List<AccountResponse> getAccounts();
  public Integer getBalanceByAccountNumber(String accountNumber);
  public AccountResponse createAccount(AccountRequest req);
  public void deleteAccount(Long id);
}
