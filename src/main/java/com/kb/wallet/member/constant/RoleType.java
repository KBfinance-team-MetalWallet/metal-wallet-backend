package com.kb.wallet.member.constant;

import lombok.Getter;

@Getter
public enum RoleType {
  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String authority;

  RoleType(String authority) {
    this.authority = authority;
  }
}
