package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import java.util.List;

public interface AccountService {

    List<AccountResponse> getAccounts(String email);

    void createAccount(AccountRequest req, String email);

    void deleteAccount(Long id, String email);

    Account getAccount(Long id);
}
