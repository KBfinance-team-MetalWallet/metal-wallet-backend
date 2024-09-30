package com.kb.wallet.member.dto.response;

import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
  private Long id;
  private String email;
  private String name;
  private String phone;
  private RoleType role;
  private Boolean isActivated;

  public static MemberResponse toMemberResponse(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .email(member.getEmail())
        .name(member.getName())
        .phone(member.getPhone())
        .role(member.getRole())
        .isActivated(member.getIsActivated())
        .build();
  }
}
