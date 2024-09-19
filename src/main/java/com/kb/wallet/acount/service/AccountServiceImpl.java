package com.kb.wallet.acount.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.kb.wallet.acount.domain.Account;
import com.kb.wallet.acount.dto.request.AccountRequest;
import com.kb.wallet.acount.dto.response.AccountResponse;
import com.kb.wallet.acount.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

  // TODO: MEMBER_ID 로그인 구현 시 변경 예정.
  public static final Long MEMBER_ID = 1L;
  private final AccountRepository accountRepository;

  private AccountServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public List<AccountResponse> getAccounts(){
    return accountRepository.findByMemberId(MEMBER_ID)
        .stream()
        .map(this::converToResponse)
        .collect(Collectors.toList());
  };

  @Override
  public Integer getBalanceByAccountNumber(String accountNumber){
    return accountRepository.findBalanceByMemberIdAndAccountNumber(MEMBER_ID, accountNumber);
  };

  @Override
  @Transactional
  public AccountResponse createAccount(AccountRequest req){
    Account account = new Account();
    account.setAccountNumber(req.getAccountNumber());
    account.setBalance(req.getBalance());
    account.setMemberId(MEMBER_ID);
    accountRepository.save(account);
    return converToResponse(account);
  };

  @Override
  @Transactional
  public void deleteAccount(Long id){
    AccountRepository accountRepository = this.accountRepository;
    // TODO : id 가 memberId인지 확인해야함.
    accountRepository.deleteById(id);
  }

  private AccountResponse converToResponse(Account account){
    AccountResponse response = new AccountResponse();
    response.setId(account.getId());
    response.setAccountNumber(account.getAccountNumber());
    response.setBalance(account.getBalance());
    response.setCreatedAt(account.getCreatedAt());
    return response;
  }
}
