package com.kb.wallet.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kb.wallet.account.domain.Account;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@Builder
@NonNull
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private Integer balance;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static List<AccountResponse> toAccountsResponseList(List<Account> accounts) {
        return accounts.stream()
                .map(account ->
                        AccountResponse.builder()
                                .id(account.getId())
                                .accountNumber(account.getNumber())
                                .balance(account.getBalance())
                                .createdAt(account.getCreatedAt())
                                .build()
                )
                .collect(Collectors.toList());
    }
}
