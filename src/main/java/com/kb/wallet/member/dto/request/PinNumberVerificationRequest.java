package com.kb.wallet.member.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PinNumberVerificationRequest {

  @NotBlank(message = "핀번호는 필수 입력 항목입니다.")
  @Size(min = 6, max = 6, message = "핀번호는 6자리여야 합니다.")
  private String pinNumber;
}
