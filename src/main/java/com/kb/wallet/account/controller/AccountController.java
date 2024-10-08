package com.kb.wallet.account.controller;

import com.kb.wallet.account.dto.request.AccountRequest;
import com.kb.wallet.account.dto.response.AccountResponse;
import com.kb.wallet.account.dto.response.TransactionRecordResponse;
import com.kb.wallet.account.service.AccountService;
import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts")
@Slf4j
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping
  public ApiResponse<List<AccountResponse>> getAccounts(
      @AuthenticationPrincipal Member member) {
    return ApiResponse.ok(accountService.getAccounts(member.getEmail()));
  }

  @GetMapping("/{accountId}")
  public ApiResponse<AccountResponse> getSingleAccount(
      @AuthenticationPrincipal Member member,
      @PathVariable(name = "accountId") Long accountId) {
    AccountResponse accountResponse = accountService.getSingleAccount(member.getEmail(), accountId);
    return ApiResponse.ok(accountResponse);
  }

  @PostMapping
  public ApiResponse<Void> createAccount(
      @AuthenticationPrincipal Member member,
      @RequestBody @Valid AccountRequest accountRequest) {
    accountService.createAccount(accountRequest, member.getEmail());
    return ApiResponse.ok();
  }

  @DeleteMapping("/{accountId}")
  public ApiResponse<Void> deleteAccount(
      @AuthenticationPrincipal Member member,
      @PathVariable(name = "accountId") Long accountId) {
    accountService.deleteAccount(accountId, member.getEmail());
    return ApiResponse.ok();
  }

  @GetMapping("/{accountId}/transaction-records")
  public ApiResponse<CursorResponse<TransactionRecordResponse>> getAccountTransactionRecords(
      @AuthenticationPrincipal Member member,
      @PathVariable(name = "accountId") Long accountId,
      @RequestParam(name = "cursor", required = false) Long cursor,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    CursorResponse<TransactionRecordResponse> transactionRecords = accountService.getAccountTransactionRecords(
        member.getEmail(), accountId, cursor, size);
    return ApiResponse.ok(transactionRecords);
  }
}

