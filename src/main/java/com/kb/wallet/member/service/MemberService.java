package com.kb.wallet.member.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;

public interface MemberService {

    RegisterMemberResponse registerMember(RegisterMemberRequest request);

    Member getMemberByEmail(String email);
}
