package com.kb.wallet.member.domain;

import com.kb.wallet.member.constant.RoleType;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 10)
  private String name;

  @Column(unique = true, length = 11)
  private String phone;

  @Column(nullable = false)
  private String pinNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType role;

  @Column(nullable = false)
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

  // UserDetails 인터페이스 메서드 구현
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return email; // 이메일을 사용자명으로 사용
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActivated;
  }
}
