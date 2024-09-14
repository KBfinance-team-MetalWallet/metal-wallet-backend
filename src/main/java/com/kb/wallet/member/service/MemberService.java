package com.kb.wallet.member.service;
import com.kb.wallet.member.domain.Member;
import java.util.Optional;

public interface MemberService {

  Optional<Member> getMemberById(Long id);

  Optional<Member> getMemberById2(Long id);

  Member createMember(Member member);
}
