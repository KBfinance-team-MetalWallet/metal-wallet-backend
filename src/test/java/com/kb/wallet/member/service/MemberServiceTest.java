package com.kb.wallet.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder encoder;

  @InjectMocks
  private MemberServiceImpl memberService;

  private Member testMember;

  @BeforeEach
  void setUp() {
    testMember = Member.builder()
      .id(1L)
      .email("test@gmail.com")
      .isActivated(true)
      .name("테스트")
      .password("encodedPassword")
      .phone("01011111111")
      .pinNumber("encodedPinNumber")
      .role(RoleType.USER)
      .build();
  }

  @Test
  @DisplayName("회원가입 성공 케이스")
  void testRegisterMember_Success() {
    // given
    RegisterMemberRequest request = RegisterMemberRequest
      .builder()
      .email("test@gmail.com")
      .name("테스트")
      .phone("01011111111")
      .password("1111")
      .pinNumber("111111")
      .build();

    when(encoder.encode("111111")).thenReturn("encodedPinNumber");
    when(encoder.encode("1111")).thenReturn("encodedPassword");
    when(memberRepository.save(any(Member.class))).thenReturn(testMember);

    // when
    RegisterMemberResponse response = memberService.registerMember(request);

    // then
    assertEquals(1L, response.getId());
    assertEquals("test@gmail.com", response.getEmail());
    assertEquals("테스트", response.getName());

    verify(memberRepository, times(1)).save(any(Member.class));
    verify(encoder, times(2)).encode(anyString());
  }

  @Test
  @DisplayName("이메일로 회원 조회 성공 케이스")
  void testGetMemberByEmail_Success() {
    // given
    String email = testMember.getEmail();
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));

    // when
    Member member = memberService.getMemberByEmail(email);

    // then
    assertThat(member).isEqualTo(testMember);
    verify(memberRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName("이메일로 회원 조회 실패 케이스")
  void testGetMemberByEmail_NotFound() {
    // given
    String email = testMember.getEmail();
    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.getMemberByEmail(email);
    });

    assertEquals(ErrorCode.MEMBER_NOT_FOUND_ERROR, exception.getErrorCode());
    verify(memberRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName("핀 번호 검증 성공 케이스")
  void testCheckPinNumber_Success() {
    // given
    String email = testMember.getEmail();
    PinNumberVerificationRequest passwordRequest = new PinNumberVerificationRequest(
      "validPinNumber");
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));
    when(encoder.matches(passwordRequest.getPinNumber(), testMember.getPinNumber())).thenReturn(
      true);

    // when
    memberService.checkPinNumber(email, passwordRequest);

    // then
    verify(memberRepository, times(1)).findByEmail(email);
    verify(encoder, times(1)).matches(passwordRequest.getPinNumber(), testMember.getPinNumber());
  }

  @Test
  @DisplayName("핀 번호 검증 실패 케이스")
  void testCheckPassword_NotMatch() {
    // given
    String email = testMember.getEmail();
    PinNumberVerificationRequest passwordRequest = new PinNumberVerificationRequest(
      "invalidPinNumber");
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));
    when(encoder.matches(passwordRequest.getPinNumber(), testMember.getPinNumber())).thenReturn(
      false);

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.checkPinNumber(email, passwordRequest);
    });

    assertEquals(ErrorCode.PIN_NUMBER_NOT_MATCH, exception.getErrorCode());
    verify(memberRepository, times(1)).findByEmail(email);
    verify(encoder, times(1)).matches(passwordRequest.getPinNumber(), testMember.getPinNumber());
  }

  @Test
  @DisplayName("이메일 중복 체크 성공 케이스")
  void testCheckEmail_Success() {
    // given
    String email = testMember.getEmail();

    // when
    memberService.checkEmail(email);

    // then
    verify(memberRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("이메일 중복 체크 실패 케이스")
  void testCheckEmail_AlreadyExists() {
    // given
    String email = testMember.getEmail();
    when(memberRepository.existsByEmail(email)).thenReturn(true);

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.checkEmail(email);
    });
    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    verify(memberRepository, times(1)).existsByEmail(email);
  }

  @Test
  @DisplayName("전화번호 중복 체크 성공 케이스")
  void testCheckPhoneNumber_Success() {
    // given
    String phoneNumber = testMember.getPhone();
    when(memberRepository.existsByPhone(phoneNumber)).thenReturn(false);

    // when
    memberService.checkPhoneNumber(phoneNumber);

    // then
    verify(memberRepository, times(1)).existsByPhone(phoneNumber);
  }

  @Test
  @DisplayName("전화번호 중복 체크 실패 케이스")
  void testCheckPhoneNumber_AlreadyExists() {
    // given
    String phoneNumber = testMember.getPhone();
    when(memberRepository.existsByPhone(phoneNumber)).thenReturn(true);

    // when, then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.checkPhoneNumber(phoneNumber);
    });
    assertEquals(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS,
      exception.getErrorCode());
    verify(memberRepository, times(1)).existsByPhone(phoneNumber);
  }
}
