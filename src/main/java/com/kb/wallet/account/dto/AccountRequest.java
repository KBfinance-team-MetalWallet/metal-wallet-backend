package com.kb.wallet.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
  private String accountNumber;
  private Integer balance;
}
