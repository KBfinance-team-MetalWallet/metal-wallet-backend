package com.kb.wallet.member.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;

public interface MemberService {

  RegisterMemberResponse registerMember(RegisterMemberRequest request);

  Member getMemberByEmail(String email);

  void checkPinNumber(String email, PinNumberVerificationRequest passwordRequest);

  void checkEmail(String email);

  void checkPhoneNumber(String phoneNumber);
}
