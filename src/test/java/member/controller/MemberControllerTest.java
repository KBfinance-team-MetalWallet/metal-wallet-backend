package member.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.jwt.JwtFilter;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.controller.MemberController;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.LoginMemberRequest;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.service.MemberServiceImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

  @Mock
  private MemberServiceImpl memberService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private TokenProvider tokenProvider;

  @InjectMocks
  private MemberController memberController;

  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("회원가입 성공 테스트")
  void testRegisterMember_Success() {
    // given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email("test@gmail.com")
      .password("testPassword")
      .name("테스트")
      .phone("01011111111")
      .pinNumber("111111")
      .build();

    RegisterMemberResponse response = new RegisterMemberResponse(1L, "test@gmail.com", "테스트");
    when(memberService.registerMember(request)).thenReturn(response);

    // when
    ApiResponse<RegisterMemberResponse> result = memberController.registerMember(request);

    // then
    assertEquals("Status code should be 200", 200, result.getResultCode());
    assertNotNull(result.getResult(), "Result should not be null");
    assertEquals("Member ID should match", response.getId(), result.getResult().getId());
    assertEquals("Email should match", response.getEmail(), result.getResult().getEmail());
    assertEquals("Name should match", response.getName(), result.getResult().getName());

    verify(memberService).registerMember(any(RegisterMemberRequest.class));
  }

  @Test
  @DisplayName("회원가입 시 NULL 필드 테스트")
  void testRegisterMember_NullFields() {
    //given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email(null)
      .password(null)
      .name(null)
      .phone(null)
      .pinNumber(null)
      .build();

    // when
    Set<ConstraintViolation<RegisterMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("총 5개의 유효성 검사와 예외 처리가 발생", 5, violations.size());
  }

  @Test
  @DisplayName("회원가입 시 잘못된 이메일 형식 테스트")
  void testRegisterMember_InvalidEmail() {
    //given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email("InvalidEmail")
      .password("testPassword")
      .name("테스트")
      .phone("01011111111")
      .pinNumber("111111")
      .build();

    // when
    Set<ConstraintViolation<RegisterMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("유효하지 않은 이메일 형식 예외 처리가 발생", 1, violations.size());
    assertEquals("유효하지 않은 이메일 형식 예외 처리 메시지", "유효한 이메일 주소를 입력해 주세요.",
      violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("회원가입 시 잘못된 이름 테스트")
  void testRegisterMember_InvalidName() {
    //given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email("test@gmail.com")
      .password("testPassword")
      .name("테스트테스트테스트테스트")
      .phone("01011111111")
      .pinNumber("111111")
      .build();

    // when
    Set<ConstraintViolation<RegisterMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("이름의 최대 길이를 초과할 시 예외 처리가 발생", 1, violations.size());
    assertEquals("이름의 최대 길이를 초과할 시 예외 처리 메시지", "이름의 길이는 10자 이내여야 합니다.",
      violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("회원가입 시 잘못된 전화번호 테스트")
  void testRegisterMember_InvalidPhone() {
    //given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email("test@gmail.com")
      .password("testPassword")
      .name("테스트")
      .phone("010111111111111")
      .pinNumber("111111")
      .build();

    // when
    Set<ConstraintViolation<RegisterMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("전화번호의 길이가 11자리가 아닐 시 예외 처리가 발생", 1, violations.size());
    assertEquals("전화번호의 길이가 11자리가 아닐 시 예외 처리 메시지", "전화번호는 숫자(11자리)만 입력해주세요.",
      violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("회원가입 시 잘못된 핀번호 테스트")
  void testRegisterMember_InvalidPinNumber() {
    //given
    RegisterMemberRequest request = RegisterMemberRequest.builder()
      .email("test@gmail.com")
      .password("testPassword")
      .name("테스트")
      .phone("01011111111")
      .pinNumber("1111111111")
      .build();

    // when
    Set<ConstraintViolation<RegisterMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("핀번호의 길이가 6자리가 아닐 시 예외 처리가 발생", 1, violations.size());
    assertEquals("핀번호의 길이가 6자리가 아닐 시 예외 처리 메시지", "핀번호는 6자리여야 합니다.",
      violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("로그인 성공 테스트")
  void testLoginMember_Success() {
    // given
    LoginMemberRequest request = new LoginMemberRequest("test@gmail.com", "testPassword");

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@gmail.com");

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    List<GrantedAuthority> authorities = Collections.singletonList(authority);
    doReturn(authorities).when(authentication).getAuthorities();

    String accessToken = "mockJwtToken";
    when(tokenProvider.createToken(anyString(), anyString())).thenReturn(accessToken);

    // when
    ResponseEntity<HashMap<String, Object>> response = memberController.loginMember(request);

    // then
    assertEquals("응답 상태는 200 OK 여야 합니다.", HttpStatus.OK, response.getStatusCode());
    HashMap<String, Object> body = response.getBody();
    assertEquals("결과는 success 여야 합니다.", "success", body.get("result"));
    assertEquals("AccessToken은 일치해야 합니다.", accessToken,
      body.get("accessToken")); // Use the mocked token
    HttpHeaders headers = response.getHeaders();
    assertEquals("헤더에 JWT 토큰이 포함되어야 합니다.", "Bearer " + accessToken,
      headers.getFirst(JwtFilter.AUTHORIZATION_HEADER));
  }

  @Test
  @DisplayName("로그인 인증 실패 테스트")
  void testLoginMember_Unauthorized() {
    // given
    LoginMemberRequest request = new LoginMemberRequest("test@gmail.com", "wrongPassword");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new AuthenticationException("Login failed") {
      });

    // when
    ResponseEntity<HashMap<String, Object>> response = memberController.loginMember(request);

    // then
    assertEquals("응답 상태는 UNAUTHORIZED 여야 합니다.", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    HashMap<String, Object> body = response.getBody();
    assertEquals("결과는 fail 여야 합니다.", "fail", body.get("result"));
    assertTrue("메시지는 'Login failed: Login failed' 여야 합니다.",
      body.get("message").toString().startsWith("Login failed:"));
  }

  @Test
  @DisplayName("로그인 시 NULL 필드 테스트")
  void testLoginMember_NullFields() {
    //given
    LoginMemberRequest request = new LoginMemberRequest(null, null);

    // when
    Set<ConstraintViolation<LoginMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("총 2개의 유효성 검사와 예외 처리가 발생", 2, violations.size());
  }

  @Test
  @DisplayName("로그인 시 잘못된 이메일 형식 테스트")
  void testLoginMember_InvalidEmail() {
    //given
    LoginMemberRequest request = new LoginMemberRequest("InvalidEmail", "password");

    // when
    Set<ConstraintViolation<LoginMemberRequest>> violations = validator.validate(request);

    // then
    assertEquals("유효하지 않은 이메일 형식 예외 처리가 발생", 1, violations.size());
    assertEquals("유효하지 않은 이메일 형식 예외 처리 메시지", "유효한 이메일 주소를 입력해 주세요.",
      violations.iterator().next().getMessage());
  }

  @Test
  @DisplayName("핀 번호 검증 성공 테스트")
  void testCheckPinNumber_Success() {
    // given
    PinNumberVerificationRequest request = new PinNumberVerificationRequest("123456");
    Member member = mock(Member.class);
    when(member.getEmail()).thenReturn(
      "test@gmail.com");

    // when
    ApiResponse<Void> response = memberController.checkPinNumber(member,
      request);

    // then
    assertEquals("응답 상태는 200 이어야 합니다.", 200, response.getResultCode());
    assertEquals("응답 본문은 OK 이어야 합니다.", "OK",
      response.getResultMsg()); // Check the response message

    // Verify that the service method was called once with the correct parameters
    verify(memberService, times(1)).checkPinNumber(eq("test@gmail.com"), eq(request));
  }

  @Test
  @DisplayName("핀 번호 검증 실패 테스트")
  void testCheckPinNumber_NotMatch() {
    // given
    PinNumberVerificationRequest request = new PinNumberVerificationRequest(
      "wrongPin");

    Member member = mock(Member.class);
    when(member.getEmail()).thenReturn("test@gmail.com");

    doThrow(new IllegalArgumentException("Invalid pin number"))
      .when(memberService)
      .checkPinNumber(eq("test@gmail.com"), any(PinNumberVerificationRequest.class));

    // when
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      memberController.checkPinNumber(member, request);
    });

    // then
    assertEquals("Invalid pin number", "Invalid pin number",
      exception.getMessage()); // Verify the exception message
    verify(memberService, times(1)).checkPinNumber(eq("test@gmail.com"), eq(request));
  }

  @Test
  @DisplayName("핀 번호 검증 시 NULL 필드 테스트")
  void testCheckPinNumber_NullFields() {
    //given
    PinNumberVerificationRequest request = new PinNumberVerificationRequest(null);

    // when
    Set<ConstraintViolation<PinNumberVerificationRequest>> violations = validator.validate(request);

    // then
    assertEquals("총 1개의 유효성 검사와 예외 처리가 발생", 1, violations.size());
  }
}
