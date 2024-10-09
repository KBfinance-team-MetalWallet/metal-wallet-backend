package com.kb.wallet.member.service;

import static com.kb.wallet.global.common.status.ErrorCode.MEMBER_EMAIL_NOT_FOUND;
import static com.kb.wallet.global.common.status.ErrorCode.PIN_NUMBER_NOT_MATCH;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.MemberResponse;
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
  public MemberResponse findById(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND_ERROR));
    return MemberResponse.toMemberResponse(member);
  }

  @Override
  @Transactional(transactionManager = "jpaTransactionManager")
  public RegisterMemberResponse registerMember(RegisterMemberRequest request) {
    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new CustomException(ErrorCode.MEMBER_STATUS_INVALID, "이미 존재하는 이메일입니다");
    }

    if (memberRepository.existsByPhone(request.getPhone())) {
      throw new CustomException(ErrorCode.MEMBER_STATUS_INVALID, "이미 존재하는 핸드폰 번호입니다");
    }

    String encodedPin = encoder.encode(request.getPinNumber());
    String encodedPassword = encoder.encode(request.getPassword());
    Member member = new Member(request.getEmail(), request.getName(), request.getPhone(),
        encodedPassword, encodedPin);

    memberRepository.save(member);
    return new RegisterMemberResponse(member.getId(), member.getEmail(), member.getName());
  }

  @Override
  public Member getMemberByEmail(String email) {
    return memberRepository.getByEmail(email)
        .orElseThrow(() -> new CustomException(MEMBER_EMAIL_NOT_FOUND));
  }

  @Override
  public void checkPassword(String email, PinNumberVerificationRequest passwordRequest) {
    Member member = getMemberByEmail(email);
    if (!encoder.matches(passwordRequest.getPinNumber(), member.getPinNumber())) {
      throw new CustomException(PIN_NUMBER_NOT_MATCH);
    }
  }
}