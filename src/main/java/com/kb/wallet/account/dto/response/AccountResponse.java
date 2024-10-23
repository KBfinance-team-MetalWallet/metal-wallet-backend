package com.kb.wallet.account.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kb.wallet.account.domain.Account;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;


@Getter
@Builder
@NonNull
public class AccountResponse {

  private Long id;
  private String accountNumber;
  private Integer balance;
  private String bankLogo;
  private String bankName;
  private String bankColor;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;


  public static List<AccountResponse> toAccountsResponseList(List<Account> accounts) {
    return accounts.stream()
      .map(account ->
        AccountResponse.builder()
          .id(account.getId())
          .accountNumber(account.getNumber())
          .balance(account.getBalance())
          .bankLogo(account.getBankLogo())
          .bankColor(account.getBankColor())
          .bankName(String.valueOf(account.getBankName().getBank()))
          .createdAt(account.getCreatedAt())
          .build()
      )
      .collect(Collectors.toList());
  }

  public static AccountResponse toAccountResponse(Account account) {
    return AccountResponse.builder()
      .id(account.getId())
      .accountNumber(account.getNumber())
      .balance(account.getBalance())
      .bankLogo(account.getBankLogo())
      .bankName(account.getBankName().toString())
      .bankColor(account.getBankColor())
      .createdAt(account.getCreatedAt())
      .build();
  }
}
