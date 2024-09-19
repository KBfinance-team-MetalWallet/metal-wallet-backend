package com.kb.wallet.acount.controller;


import com.kb.wallet.acount.dto.request.AccountRequest;
import com.kb.wallet.acount.dto.response.AccountResponse;
import com.kb.wallet.acount.service.AccountService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  // 1. 전체 계좌 조회
  @GetMapping
  public ResponseEntity<List<AccountResponse>> getAccounts() {
    return ResponseEntity.ok(accountService.getAccounts());
  }

  //2. 특정 계좌 잔액 조회
  @GetMapping("/{accountNumber}/balance")
  public ResponseEntity<Integer> getBalanceByAccountNumber(@PathVariable String accountNumber){
    return ResponseEntity.ok(accountService.getBalanceByAccountNumber(accountNumber));
  }

  //3. 계좌 생성
  @PostMapping
  public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest accountRequest) {
    return ResponseEntity.ok(accountService.createAccount(accountRequest));
  }

  //4. 계좌 삭제
  @DeleteMapping
  public ResponseEntity<AccountResponse> deleteAccount(@RequestBody Long id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }
}

