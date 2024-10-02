package com.kb.wallet.account.service;

import static com.kb.wallet.global.common.status.ErrorCode.ACCOUNT_NOT_MATCH;

import com.kb.wallet.account.constant.BankName;
import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.request.AccountRequest;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.account.repository.AccountRepository;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final MemberService memberService;

  @Override
  public List<AccountResponse> getAccounts(String email) {
    List<Account> accounts = accountRepository.findAllByMember(email);
    return AccountResponse.toAccountsResponseList(accounts);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void createAccount(AccountRequest req, String email) {
    Member member = memberService.getMemberByEmail(email);
    Account account = Account.builder()
        .number(req.getAccountNumber())
        .balance(req.getBalance())
        .bankName(BankName.valueOf(req.getBankName()))
        .member(member)
        .build();
    accountRepository.save(account);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public void deleteAccount(Long id, String email) {
    Member member = memberService.getMemberByEmail(email);
    Account account = getSingleAccount(id);
    if (!Objects.equals(member.getId(), account.getMember().getId())) {
      throw new CustomException(ACCOUNT_NOT_MATCH);
    }
    accountRepository.delete(account);
  }

  @Override
  public Account getSingleAccount(Long id) {
    return accountRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_ERROR) {
        });
  }

  @Override
  public Page<TransactionRecordResponse> getAccountTransactionRecords(Member member, Long accountId,
      int page, int size) {
    return null;
  }
}
