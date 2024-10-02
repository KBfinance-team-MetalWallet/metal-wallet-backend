package com.kb.wallet.account.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BankName {
  KOOKKMIN("국민"),
  SHINHAN("신한"),
  WOORI("우리"),
  HANA("하나");

  private final String bank;

}
