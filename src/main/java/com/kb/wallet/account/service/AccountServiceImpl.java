package com.kb.wallet.account.service;

import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.exception.AccountNotFoundException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.AccountResponse;
import com.kb.wallet.account.repository.AccountRepository;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

  // TODO: MEMBER_ID 로그인 구현 시 변경 예정.
  private final AccountRepository accountRepository;
  private final MemberService memberService;

  private AccountServiceImpl(AccountRepository accountRepository, MemberService memberService) {
    this.accountRepository = accountRepository;
    this.memberService = memberService;
  }

  @Override
  public List<AccountResponse> getAccounts() {
    //TODO: Tmp Member Data -> AuthenticationUtils
    Member member = new Member();
    member.setId(1L);
    //TODO: Global Exception
    List<Account> accounts = accountRepository.findAllByMember(member);
    return AccountResponse.toAccountsResponseList(accounts);
  }

  @Override
  public AccountResponse getBalanceByAccountId(Long accountNumber) {
    //TODO: Tmp Member Data (로그인 구현 시 삭제 예정)
    Member member = new Member();
    member.setId(1L);
    Account account = accountRepository.findByMemberAndId(member, accountNumber)
        .orElseThrow(() -> new RuntimeException("Account not found with account number"));
    return AccountResponse.toAccountResponse(account);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void createAccount(AccountRequest req) {
    Member member = new Member();
    member.setId(1L);
    Account account = Account.builder()
        .number(req.getAccountNumber())
        .balance(req.getBalance())
        .member(member)
        .build();
    accountRepository.save(account);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void deleteAccount(Long id) {
    // TODO : id 가 memberId인지 확인해야함.
    accountRepository.deleteById(id);
  }
}
