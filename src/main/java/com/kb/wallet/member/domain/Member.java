package com.kb.wallet.member.domain;

import com.kb.wallet.member.constant.RoleType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotNull(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 10, message = "이름의 길이는 10자 이내여야 합니다.")
    private String name;


    @Column(unique = true)
    @NotNull(message = "전화번호는 필수 입력 항목입니다.")
    @Size(min = 11, max = 11, message = "전화번호는 숫자(11자리)만 입력해주세요.")
    private String phone;


    @NotNull(message = "핀번호는 필수 입력 항목입니다.")
    private String pinNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @NotNull
    private Boolean isActivated;

    public Member(String email, String name, String phone, String encodedPassword,
            String encodedPin) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = encodedPassword;
        this.pinNumber = encodedPin;
        this.role = RoleType.USER;
        this.isActivated = true;
    }
}
