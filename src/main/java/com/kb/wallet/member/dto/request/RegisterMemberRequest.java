package com.kb.wallet.member.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterMemberRequest {

    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotNull(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 10, message = "이름의 길이는 10자 이내여야 합니다.")
    private String name;

    @NotNull(message = "전화번호는 필수 입력 항목입니다.")
    @Size(min = 11, max = 11, message = "전화번호는 숫자(11자리)만 입력해주세요.")
    private String phone;

    @NotNull(message = "핀번호는 필수 입력 항목입니다.")
    @Size(min = 6, max = 6, message = "핀번호는 6자리여야 합니다.")
    private String pinNumber;
}
