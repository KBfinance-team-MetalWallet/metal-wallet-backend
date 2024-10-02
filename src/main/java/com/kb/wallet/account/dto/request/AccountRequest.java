package com.kb.wallet.account.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
  @NotBlank(message = "계좌 번호는 필수 입력 항목입니다.")
  private String accountNumber;
  private Integer balance;
  private String bankName;
}
