package com.kb.wallet.test.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberMapper;
import com.kb.wallet.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;

  @Override
  public Optional<Member> getMemberById(Long id) {
    return memberRepository.findById(id);
  }

  @Override
  public Optional<Member> getMemberById2(Long id) {
    Member member = memberMapper.findById(id);
    return Optional.ofNullable(member);
  }

  @Override
  public Member createMember(Member member) {
    return memberRepository.save(member);
  }
}
