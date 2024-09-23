package com.kb.wallet.member.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginMemberRequest {

    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
