package com.kb.wallet.member.dto.response;

import lombok.Getter;

@Getter
public class RegisterMemberResponse {

  private final long id;
  private final String email;
  private final String name;

  public RegisterMemberResponse(long id, String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
  }
}
