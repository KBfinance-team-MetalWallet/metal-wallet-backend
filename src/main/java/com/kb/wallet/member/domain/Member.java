package com.kb.wallet.member.domain;

import com.kb.wallet.member.constant.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
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

  @NotNull(message = "이름은 필수 입력 항목입니다.")
  @Size(max = 10, message = "이름의 길이는 10자 이내여야 합니다.")
  private String name;

  @Column(unique = true)
  @NotNull(message = "전화번호는 필수 입력 항목입니다.")
  @Size(min = 11, max = 11, message = "전화번호는 숫자(11자리)만 입력해주세요.")
  private String phone;

  @NotNull(message = "핀번호는 필수 입력 항목입니다.")
  @Size(min = 6, max = 6, message = "핀번호는 6자리여야 합니다.")
  private String pinNumber;

  @NotNull
  @Enumerated(EnumType.STRING)
  private RoleType role;

  @NotNull
  private Boolean isActivated;
}
