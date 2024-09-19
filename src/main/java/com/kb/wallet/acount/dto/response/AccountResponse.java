package com.kb.wallet.acount.dto.response;

import java.time.LocalDateTime;
import lombok.Setter;

@Setter
public class AccountResponse {
  private Long id;
  private String accountNumber;
  private Integer balance;
  private LocalDateTime createdAt;
}
