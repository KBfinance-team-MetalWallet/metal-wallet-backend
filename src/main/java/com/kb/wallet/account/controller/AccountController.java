package com.kb.wallet.account.controller;

import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import com.kb.wallet.account.service.AccountService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/accounts")
@Slf4j
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  /**
   * TODO: 로그인 autentication 적용 예정
   * @return
   */
  @GetMapping
  public ResponseEntity<List<AccountResponse>> getAccounts() {
    return ResponseEntity.ok(accountService.getAccounts());
  }

  @GetMapping("/{accountNumber}/balance")
  public ResponseEntity<AccountResponse> getBalanceByAccountNumber(@PathVariable(name = "number") String accountNumber){
    AccountResponse accountResponse = accountService.getBalanceByAccountNumber(accountNumber);
    return ResponseEntity.ok(accountResponse);
  }

  @PostMapping
  public ResponseEntity<Void> createAccount(@RequestBody AccountRequest accountRequest) {
    accountService.createAccount(accountRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable(name = "id") Long id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }
}

