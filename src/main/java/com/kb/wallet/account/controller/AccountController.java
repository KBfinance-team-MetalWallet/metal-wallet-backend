package com.kb.wallet.account.controller;

import com.kb.wallet.account.dto.AccountRequest;
import com.kb.wallet.account.dto.AccountResponse;
import com.kb.wallet.account.service.AccountService;
import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.member.domain.Member;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<List<AccountResponse>> getAccounts(
            @AuthenticationPrincipal Member member) {
        return ApiResponse.ok(accountService.getAccounts(member.getEmail()));
    }

    @PostMapping
    public ApiResponse<Void> createAccount(
            @AuthenticationPrincipal Member member,
            @RequestBody AccountRequest accountRequest) {
        accountService.createAccount(accountRequest, member.getEmail());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccount(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "id") Long id) {
        accountService.deleteAccount(id, member.getEmail());
        return ApiResponse.ok();
    }
}

