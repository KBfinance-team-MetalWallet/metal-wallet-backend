package com.kb.wallet.account.service;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import com.kb.wallet.account.repository.AccountRepository;
import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberService memberService;

    private AccountServiceImpl(AccountRepository accountRepository, MemberService memberService) {
        this.accountRepository = accountRepository;
        this.memberService = memberService;
    }

    @Override
    public List<AccountResponse> getAccounts(String email) {
        Member member = memberService.getMemberByEmail(email);
        List<Account> accounts = accountRepository.findAllByMember(member);
        return AccountResponse.toAccountsResponseList(accounts);
    }

    @Override
    @Transactional(transactionManager = "jpaTransactionManager")
    public void createAccount(AccountRequest req, String email) {
        Member member = memberService.getMemberByEmail(email);
        Account account = Account.builder()
                .number(req.getAccountNumber())
                .balance(req.getBalance())
                .member(member)
                .build();
        accountRepository.save(account);
    }

    @Override
    @Transactional(transactionManager = "jpaTransactionManager")
    public void deleteAccount(Long id, String email) {
        if (!isAccountOwnedByMember(id, email)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR) {
            };
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_ERROR) {
                });
        accountRepository.delete(account);
    }

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_ERROR) {
                });
    }

    public boolean isAccountOwnedByMember(Long id, String email) {
        Account account = getAccount(id);
        return account.getMember().getEmail().equals(email);
    }
}
