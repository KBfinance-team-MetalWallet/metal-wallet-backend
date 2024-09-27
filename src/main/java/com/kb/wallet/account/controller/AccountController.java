package com.kb.wallet.account.controller;

import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import com.kb.wallet.account.service.AccountService;
import com.kb.wallet.member.domain.Member;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<AccountResponse>> getAccounts(
            @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(accountService.getAccounts(member.getEmail()));
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(
            @AuthenticationPrincipal Member member,
            @RequestBody AccountRequest accountRequest) {
        accountService.createAccount(accountRequest, member.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "id") Long id) {
        accountService.deleteAccount(id, member.getEmail());
        return ResponseEntity.noContent().build();
    }
}

