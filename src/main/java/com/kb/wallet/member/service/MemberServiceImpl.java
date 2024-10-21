package com.kb.wallet.member.service;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder encoder;

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public RegisterMemberResponse registerMember(RegisterMemberRequest request) {
    checkEmail(request.getEmail());
    checkPhoneNumber(request.getPhone());

    String encodedPin = encoder.encode(request.getPinNumber());
    String encodedPassword = encoder.encode(request.getPassword());
    Member member = new Member(request.getEmail(), request.getName(), request.getPhone(),
      encodedPassword, encodedPin);

    memberRepository.save(member);
    return new RegisterMemberResponse(member.getId(), member.getEmail(), member.getName());
  }

  @Override
  public Member getMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
      .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND_ERROR));
  }

  @Override
  public void checkPassword(String email, PinNumberVerificationRequest passwordRequest) {
    Member member = getMemberByEmail(email);
    if (!encoder.matches(passwordRequest.getPinNumber(), member.getPinNumber())) {
      throw new CustomException(ErrorCode.PIN_NUMBER_NOT_MATCH);
    }
  }

  @Override
  public void checkEmail(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

  @Override
  public void checkPhoneNumber(String phoneNumber) {
    if (memberRepository.existsByPhone(phoneNumber)) {
      throw new CustomException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
    }
  }
}