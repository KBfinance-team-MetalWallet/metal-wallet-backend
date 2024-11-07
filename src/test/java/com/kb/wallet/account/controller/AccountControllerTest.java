package com.kb.wallet.account.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kb.wallet.account.constant.TransactionType;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.account.service.AccountServiceImpl;
import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private AccountController accountController;

  @Mock
  private AccountServiceImpl accountService;

  private Long accountId;
  private String userEmail;

  @Mock
  private Member mockMember;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

    accountId = 1L;
    userEmail = "test@exmaple.com";
  }

  @Test
  @DisplayName("@AuthenticationPrincipal로 인증된 사용자 정보 가져오기 성공")
  void testAuthenticationPrincipal_Success() throws Exception {
    //given
    Member member = Member.builder().email(userEmail).build();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(member, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // when & then
    mockMvc.perform(get("/accounts/{accountId}", accountId))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("단건 계좌 조회 컨트롤러 동작 성공 - 단위 테스트")
  void testGetAccount_Success_UnitTest() {
    // given
    Long accountId = 1L; // 테스트용 accountId 설정
    AccountResponse accountResponse = AccountResponse.builder()
        .accountNumber("1234567890")
        .build();

    Member mockMember = mock(Member.class);
    when(mockMember.getEmail()).thenReturn("test@example.com");

    // 서비스 계층 메서드 호출에 대한 스텁 설정
    given(accountService.getAccountById(mockMember.getEmail(), accountId)).willReturn(accountResponse);

    // when
    ApiResponse<AccountResponse> response = accountController.getAccount(mockMember, accountId);

    // then - 결과 검증
    assertEquals("1234567890", response.getResult().getAccountNumber());
    verify(accountService, times(1)).getAccountById(mockMember.getEmail(), accountId);
  }


  @Test
  @DisplayName("사용자 계좌 목록 조회 성공")
  void testGetAccounts_Success() {
    // given
    Member mockMember = mock(Member.class);
    when(mockMember.getEmail()).thenReturn(userEmail);

    AccountResponse response1 = AccountResponse.builder().accountNumber("12345678").build();
    AccountResponse response2 = AccountResponse.builder().accountNumber("87654321").build();
    List<AccountResponse> accountResponses = Arrays.asList(response1, response2);

    // Service stubbing
    given(accountService.getAccountsByEmail(userEmail)).willReturn(accountResponses);

    // when & then
    ApiResponse<List<AccountResponse>> response = accountController.getAccounts(mockMember);

    // then - 결과 검증
    assertEquals(2, response.getResult().size());
    assertEquals("12345678", response.getResult().get(0).getAccountNumber());
    assertEquals("87654321", response.getResult().get(1).getAccountNumber());

    verify(accountService, times(1)).getAccountsByEmail(userEmail);
  }

  @Test
  @DisplayName("계좌 거래 기록 조회 성공 - 단위 테스트")
  void testGetAccountTransactionRecords_Success() {
    // given
    Long cursor = null;
    int size = 10;

    // @AuthenticationPrincipal 로 주입될 Member 모킹
    Member mockMember = mock(Member.class);
    when(mockMember.getEmail()).thenReturn(userEmail);

    // 거래 기록 응답을 위한 Mock 데이터 설정
    List<TransactionRecordResponse> transactionRecordResponses = Arrays.asList(
        TransactionRecordResponse.builder().amount(1000L).transactionType(TransactionType.DEPOSIT)
            .vendor("가맹점1").build(),
        TransactionRecordResponse.builder().amount(2000L).transactionType(TransactionType.DEPOSIT)
            .vendor("가맹점2").build()
    );
    Long expectedNextCursor = transactionRecordResponses.get(transactionRecordResponses.size() - 1).getTransactionId();

    // 서비스 계층 메서드의 스텁 설정
    given(accountService.getTransactionRecords(userEmail, accountId, cursor, size))
        .willReturn(transactionRecordResponses);

    // when
    ApiResponse<CursorResponse<TransactionRecordResponse>> response =
        accountController.getAccountTransactionRecords(mockMember, accountId, cursor, size);

    // then
    assertNotNull(response);
    assertEquals(2, response.getResult().getData().size());
    assertEquals(expectedNextCursor, response.getResult().getNextCursor());
    verify(accountService, times(1)).getTransactionRecords(userEmail, accountId, cursor, size);
  }

}
