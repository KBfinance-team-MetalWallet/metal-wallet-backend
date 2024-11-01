package com.kb.wallet.member.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kb.wallet.member.constant.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberDomainTest {

  @Test
  @DisplayName("멤버 생성자 테스트")
  public void testMemberConstructor() {
    // given
    String email = "test@gmail.com";
    String name = "테스트";
    String password = "encodedPassword";
    String phone = "01011111111";
    String pinNumber = "encodedPin";

    // when
    Member member = new Member(email, name, phone, password, pinNumber);

    // then
    assertNotNull(member);
    assertEquals(email, member.getEmail());
    assertEquals(name, member.getName());
    assertEquals(phone, member.getPhone());
    assertEquals(password, member.getPassword());
    assertEquals(pinNumber, member.getPinNumber());
    assertEquals(RoleType.USER, member.getRole());
    assertTrue(member.getIsActivated());
  }

  @Test
  @DisplayName("멤버 빌더 테스트")
  public void testMemberBuilder() {
    // when
    Member member = Member.builder()
      .email("test@gmail.com")
      .name("테스트")
      .phone("01011111111")
      .password("encodedPassword")
      .pinNumber("encodedPin")
      .role(RoleType.USER)
      .isActivated(true)
      .build();

    // then
    assertEquals("test@gmail.com", member.getEmail());
    assertEquals("테스트", member.getName());
    assertEquals("01011111111", member.getPhone());
    assertEquals("encodedPassword", member.getPassword());
    assertEquals("encodedPin", member.getPinNumber());
    assertEquals(RoleType.USER, member.getRole());
    assertTrue(member.getIsActivated());
  }

  @Test
  @DisplayName("회원의 권한을 반환하는 테스트")
  public void testGetAuthorities() {
    // given
    Member member = new Member();
    member.setRole(RoleType.ADMIN);

    // when
    var authorities = member.getAuthorities();

    // then
    assertNotNull(authorities);
    assertEquals(1, authorities.size());
    assertEquals("ADMIN", authorities.iterator().next().getAuthority());
  }

  @Test
  @DisplayName("이메일로 사용자명을 반환하는 테스트")
  public void testGetUsername() {
    // given
    String email = "test@gmail.com";
    Member member = new Member();
    member.setEmail(email);

    // when
    String username = member.getUsername();

    // then
    assertEquals(email, username);
  }

  @Test
  @DisplayName("회원이 활성화 상태일 때 true를 반환하는 테스트")
  public void testIsEnabled() {
    // given
    Member member = new Member();
    member.setIsActivated(true);

    // when
    boolean isEnabled = member.isEnabled();

    // then
    assertTrue(isEnabled);
  }

  @Test
  @DisplayName("계정이 만료되지 않았는지 확인하는 테스트")
  public void testIsAccountNonExpired() {
    // given
    Member member = new Member();

    // when
    boolean isNonExpired = member.isAccountNonExpired();

    // then
    assertTrue(isNonExpired);
  }

  @Test
  @DisplayName("자격 증명이 만료되지 않았는지 확인하는 테스트")
  public void testIsCredentialsNonExpired() {
    // given
    Member member = new Member();

    // when
    boolean isCredentialsNonExpired = member.isCredentialsNonExpired();

    // then
    assertTrue(isCredentialsNonExpired);
  }

}
