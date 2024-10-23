package com.kb.wallet.member.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public Member loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(username)
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with email: " + username));

    log.debug("LoadedUserByEmail: {}", member);
    return member;
  }
}
