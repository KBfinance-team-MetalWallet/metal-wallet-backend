package com.kb.wallet.account.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.kb.wallet.account.constant.BankName;
import com.kb.wallet.account.constant.TransactionType;
import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.domain.TransactionRecord;
import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@WebAppConfiguration
class TransactionRecordRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TransactionRecordRepository transactionRecordRepository;

  private Account account;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    // given
    pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // repository test 시에는 DB의 GenerationType 설정으로 인해 Id 값으로 자동으로
    // 설정되기 때문에 따로 작성해주면 안된다.
    Member member = Member.builder()
        .email("test@example.com")
        .password("1234")
        .name("user").pinNumber("123456")
        .role(RoleType.USER).isActivated(true).build();
    memberRepository.save(member);

    account = Account.builder()
        .member(member)
        .number("111111")
        .bankName(BankName.KOOKMIN)
        .build();
    accountRepository.save(account);

    for (int i = 1; i <= 5; i++) {
      TransactionRecord record = TransactionRecord.builder()
          .account(account)
          .amount(1000 * i)
          .transactionType(TransactionType.DEPOSIT)
          .currentBalance(1000 * i)
          .vendor("거래처" + i)
          .build();
      transactionRecordRepository.save(record);
    }
  }

  @Test
  @DisplayName("커서 이후의 계좌 거래 내역 최신순 조회 성공")
  void testFindAllByAccountAndIdLessThanOrderByCreatedAtDesc_Success() {
    // when
    Long cursor = 10L;

    // when
    List<TransactionRecord> records = transactionRecordRepository.findAllByAccountAndIdLessThanOrderByCreatedAtDesc(
        account, cursor, pageable);

    // then
    if(!records.isEmpty()) {
      assertThat(records.get(0).getId()).isLessThan(cursor); // cursor 이하의 ID만 조회되는지 확인
      assertThat(records.get(0).getCreatedAt()).isAfter(records.get(1).getCreatedAt()); // 내림차순 정렬 확인
    }
  }

  @Test
  @DisplayName("해당 계좌에 저장된 결제 내역을 createdAt을 기준으로 최신순으로 조회 성공")
  void testFindAllByAccountOrderByCreatedAtDesc_Success() {

    // when
    List<TransactionRecord> records = transactionRecordRepository.findAllByAccountOrderByCreatedAtDesc(
        account, pageable);

    // then
    assertThat(records).hasSize(5);
    assertThat(records.get(0).getCreatedAt()).isAfter(records.get(1).getCreatedAt()); // 내림차순 정렬 확인
  }

}