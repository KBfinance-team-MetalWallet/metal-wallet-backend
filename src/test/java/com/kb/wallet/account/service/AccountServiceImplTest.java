package com.kb.wallet.account.service;

import static com.kb.wallet.account.constant.BankName.KOOKMIN;
import static com.kb.wallet.global.common.status.ErrorCode.ACCOUNT_NOT_FOUND_ERROR;
import static com.kb.wallet.global.common.status.ErrorCode.ACCOUNT_NOT_MATCH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.kb.wallet.account.domain.Account;
import com.kb.wallet.account.domain.TransactionRecord;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.account.repository.AccountRepository;
import com.kb.wallet.account.repository.TransactionRecordRepository;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @InjectMocks
  private AccountServiceImpl accountService;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private TransactionRecordRepository transactionRecordRepository;

  private Long accountId;
  private String originalEmail;

  @BeforeEach
  void setUp() {
    accountId = 1L;
    originalEmail = "test@example.com";
  }

  @Test
  @DisplayName("유효한 accountId로 계정 반환")
  void testGetAccountById_Success() {
    // given: 테스트에 필요한 데이터와 mock 객체의 동작을 정의한다.
    Account account = Account.builder()
        .id(accountId).bankName(KOOKMIN).number("1234567890").build();

    given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

    // when: 실제로 테스트하려는 메서드 호출
    Account result = accountService.getAccountById(accountId);

    // then: 예상되는 결과를 검증
    assertThat(result)
      .isEqualTo(account)
      .satisfies(res -> {
        assertThat(res.getId()).isEqualTo(accountId);
        assertThat(res.getNumber()).isEqualTo("1234567890");
        assertThat(res.getBankName()).isEqualTo(KOOKMIN);
      });

  }

  @Test
  @DisplayName("유효하지 않은 accountId로 계정 반환 실패")
  void testGetAccountById_Fail() {
    // given
    given(accountRepository.findById(accountId)).willReturn(Optional.empty());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      accountService.getAccountById(accountId);
    });

    // then
    assertEquals(ACCOUNT_NOT_FOUND_ERROR, exception.getErrorCode());
  }

  @Test
  @DisplayName("계좌 소유주 일치")
  void testGetAccountById_AccountOwnerSuccess() {
    // given
    Member member = Member.builder().email(originalEmail).build();
    Account account = Account.builder()
        .id(accountId).member(member).number("11111111111111111111").balance(0)
        .bankLogo("logo1.png").bankName(KOOKMIN).bankColor("yellow")
        .build();

    given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

    // when
    AccountResponse response = accountService.getAccountById(originalEmail, accountId);

    // then
    assertThat(response)
        .satisfies(r -> {
          assertThat(r.getId()).isEqualTo(accountId);
          assertThat(r.getAccountNumber()).isEqualTo(account.getNumber());
          assertThat(r.getBalance()).isEqualTo(account.getBalance());
          assertThat(r.getBankLogo()).isEqualTo(account.getBankLogo());
          assertThat(r.getBankName()).isEqualTo(account.getBankName().toString());
          assertThat(r.getBankColor()).isEqualTo(account.getBankColor());
        });

  }

  @Test
  @DisplayName("계좌 소유주 불일치")
  void testGetAccountById_AccountOwnerFail() {
    // given
    String fakeEmail = "fake@example.com";

    Member member = Member.builder().email(originalEmail).build();
    Account account = Account.builder().member(member).build();

    given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      accountService.getAccountById(fakeEmail, accountId);
    });

    // then
    assertEquals(ACCOUNT_NOT_MATCH, exception.getErrorCode());
  }

  @Test
  @DisplayName("이메일로 계좌 여러건 조회 성공")
  void testGetAccountsByEmail_Success() {
    // given

    Account account1 = Account.builder()
        .id(1L).number("123456").balance(1000)
        .bankLogo("logo1.png").bankName(KOOKMIN).bankColor("yellow")
        .build();

    Account account2 = Account.builder()
        .id(2L).number("654321").balance(2000)
        .bankLogo("logo2.png").bankName(KOOKMIN).bankColor("yellow")
        .build();

    List<Account> accounts = Arrays.asList(account1, account2);
    given(accountRepository.findAllByEmail(originalEmail)).willReturn(accounts);

    // when
    List<AccountResponse> responses = accountService.getAccountsByEmail(originalEmail);

    // then
    assertThat(responses)
        .satisfiesAnyOf(r1 -> {
              assertThat(r1.get(0).getId()).isEqualTo(account1.getId());
              assertThat(r1.get(0).getAccountNumber()).isEqualTo(account1.getNumber());
              assertThat(r1.get(0).getBalance()).isEqualTo(account1.getBalance());
              assertThat(r1.get(0).getBankLogo()).isEqualTo(account1.getBankLogo());
              assertThat(r1.get(0).getBankName()).isEqualTo(account1.getBankName().getBank());
              assertThat(r1.get(0).getBankColor()).isEqualTo(account1.getBankColor());
            },
            r2 -> {
              assertThat(r2.get(1).getId()).isEqualTo(account2.getId());
              assertThat(r2.get(1).getAccountNumber()).isEqualTo(account2.getNumber());
              assertThat(r2.get(1).getBalance()).isEqualTo(account2.getBalance());
              assertThat(r2.get(1).getBankLogo()).isEqualTo(account2.getBankLogo());
              assertThat(r2.get(1).getBankName()).isEqualTo(account2.getBankName().getBank());
              assertThat(r2.get(1).getBankColor()).isEqualTo(account2.getBankColor());
            }
        );
  }

  @Test
  @DisplayName("이메일로 계좌 0건 조회 성공")
  void testGetAccountsByEmail_ZeroCaseSuccess() {
    // given
    List<Account> accounts = new ArrayList<>();
    given(accountRepository.findAllByEmail(originalEmail)).willReturn(accounts);

    // when
    List<AccountResponse> responses = accountService.getAccountsByEmail(originalEmail);

    // then
    assertTrue(responses.isEmpty());
  }

  @Test
  @DisplayName("로그인 사용자 이메일과 계좌 이용자가 일칠하는 거래 내역 첫 페이지부터 조회 성공 케이스")
  void testGetTransactionRecords_Success() {
    // given
    int size = 10;
    Member member = Member.builder().email(originalEmail).build();
    Account account = Account.builder().member(member).build();

    List<TransactionRecord> transactionRecords = List.of(
        TransactionRecord.builder().id(1L).account(account).build(),
        TransactionRecord.builder().id(2L).account(account).build()
    );

    given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
    given(transactionRecordRepository.findAllByAccountOrderByCreatedAtDesc(account,
        PageRequest.of(0, size, Sort.by("createdAt").descending())))
        .willReturn(transactionRecords);

    // when
    List<TransactionRecordResponse> responses = accountService.getTransactionRecords(
        originalEmail, accountId, null, size);

    // then
    assertEquals(transactionRecords.size(), responses.size());
    assertEquals(transactionRecords.get(0).getId(), responses.get(0).getTransactionId());
    assertEquals(transactionRecords.get(1).getId(), responses.get(1).getTransactionId());
  }

  @Test
  @DisplayName("이메일 불일치로 인한 계좌 거래 내역 조회 실패")
  void testGetTransactionRecords_EmailCheckFail() {
    // given
    int size = 10;
    String differentEmail = "differentEmail@exmaple.com";

    Member member = Member.builder().email(differentEmail).build();
    Account account = Account.builder().member(member).build();

    given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> accountService.getTransactionRecords(originalEmail, accountId, null, size));

    // then
    assertEquals(exception.getErrorCode(), ACCOUNT_NOT_MATCH);
  }

}