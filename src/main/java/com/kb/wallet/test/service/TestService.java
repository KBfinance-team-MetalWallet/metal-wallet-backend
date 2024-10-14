package com.kb.wallet.test.service;

import com.kb.wallet.member.domain.Member;
import java.util.Optional;

public interface TestService {

  Optional<Member> getMemberById(Long id);

  Optional<Member> getMemberById2(Long id);

  Member createMember(Member member);

}
