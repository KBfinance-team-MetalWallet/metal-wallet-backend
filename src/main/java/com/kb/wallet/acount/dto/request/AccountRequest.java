package com.kb.wallet.acount.dto.request;

import lombok.Getter;

@Getter
public class AccountRequest {

  private String accountNumber;
  private Integer balance;
  private Long memberId;
}
