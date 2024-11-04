package com.kb.wallet.account.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kb.wallet.account.constant.BankName;
import com.kb.wallet.account.domain.Account;
import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@WebAppConfiguration
class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("이메일로 계좌 조회 성공")
  void testFindAllByEmail_Success() {
    // given
    String testEmail = "test@example.com";

    Member member = Member.builder().email(testEmail)
        .password("1234")
        .name("user")
        .pinNumber("123456")
        .role(RoleType.USER)
        .isActivated(true)
        .build();
    memberRepository.save(member);

    Account account1 = Account.builder().number("12345678")
        .bankName(BankName.KOOKMIN).member(member).build();
    Account account2 = Account.builder().number("87654321")
        .bankName(BankName.SHINHAN).member(member).build();

    accountRepository.save(account1);
    accountRepository.save(account2);

    // when
    List<Account> accounts = accountRepository.findAllByEmail(testEmail);

    // then
    assertThat(accounts).isNotEmpty();
    assertThat(accounts.size()).isEqualTo(2);
    assertThat(accounts.get(0).getMember().getEmail()).isEqualTo(testEmail);
    assertThat(accounts.get(1).getMember().getEmail()).isEqualTo(testEmail);
  }

  @Test
  @DisplayName("accountId로 계좌 조회 성공")
  void testFindById_AccountIdExistsSuccess() {
    Account account = Account.builder()
        .number("12345678")
        .bankName(BankName.KOOKMIN)
        .build();
    accountRepository.save(account);

    Long savedAccountId = account.getId(); // 저장된 엔티티의 ID 가져오기

    Optional<Account> foundAccount = accountRepository.findById(savedAccountId);

    assertThat(foundAccount).isPresent();
    assertThat(foundAccount.get().getId()).isEqualTo(savedAccountId);
  }


  @Test
  @DisplayName("accountId로 계좌 조회 실패")
  void testFindById_AccountIdDoesNotExistFail() {
    Long accountId = 999L;

    Optional<Account> foundAccount = accountRepository.findById(accountId);

    assertThat(foundAccount).isNotPresent();
  }
}
