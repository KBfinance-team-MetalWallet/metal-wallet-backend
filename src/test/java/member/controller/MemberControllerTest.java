package member.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.member.controller.MemberController;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.service.MemberServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

  @Mock
  private MemberServiceImpl memberService;

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
}
