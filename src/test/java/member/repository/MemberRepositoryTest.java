package member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kb.wallet.global.config.AppConfig;
import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@WebAppConfiguration
@Transactional
public class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  private Member testMember;

  @BeforeEach
  void setUp() {
    testMember = Member.builder()
      .email("test@gmail.com")
      .password("password")
      .phone("01011111111")
      .pinNumber("111111")
      .name("테스트")
      .role(RoleType.USER)
      .isActivated(true)
      .build();
  }

  @Test
  @DisplayName("회원 정보를 성공적으로 저장하는 테스트")
  void testSave() {
    // when
    memberRepository.save(testMember);

    // then
    Optional<Member> foundMember = memberRepository.findByEmail(testMember.getEmail());
    assertThat(foundMember).isPresent();
    assertThat(foundMember.get().getEmail()).isEqualTo(testMember.getEmail());
    assertThat(foundMember.get().getPassword()).isEqualTo(testMember.getPassword());
    assertThat(foundMember.get().getPhone()).isEqualTo(testMember.getPhone());
    assertThat(foundMember.get().getPinNumber()).isEqualTo(testMember.getPinNumber());
    assertThat(foundMember.get().getName()).isEqualTo(testMember.getName());
    assertThat(foundMember.get().getRole()).isEqualTo(testMember.getRole());
    assertThat(foundMember.get().getIsActivated()).isEqualTo(testMember.getIsActivated());
  }

  @Test
  @DisplayName("이메일이 존재할 경우 true를 반환하는 테스트")
  void testExistsByEmail_True() {
    // given
    memberRepository.save(testMember);

    // when
    boolean exists = memberRepository.existsByEmail("test@gmail.com");

    // then
    assertTrue(exists);
  }

  @Test
  @DisplayName("이메일이 존재하지 않을 경우 false를 반환하는 테스트")
  void testExistsByEmail_False() {
    // given
    memberRepository.save(testMember);

    // when
    boolean exists = memberRepository.existsByEmail("test@naver.com");

    // then
    assertFalse(exists);
  }

  @Test
  @DisplayName("전화번호가 존재할 경우 true를 반환하는 테스트")
  void testExistsByPhone_True() {
    // given
    memberRepository.save(testMember);

    // when
    boolean exists = memberRepository.existsByPhone("01011111111");

    // then
    assertTrue(exists);
  }

  @Test
  @DisplayName("전화번호가 존재하지 않을 경우 false를 반환하는 테스트")
  void testExistsByPhone_False() {
    // given
    memberRepository.save(testMember);

    // when
    boolean exists = memberRepository.existsByPhone("01099999999");

    // then
    assertFalse(exists);
  }

  @Test
  @DisplayName("이메일로 회원을 조회할 때 회원 정보를 성공적으로 반환하는 테스트")
  void testFindByEmail() {
    // given
    memberRepository.save(testMember);

    // when
    Optional<Member> foundMember = memberRepository.findByEmail("test@gmail.com");

    // then
    assertTrue(foundMember.isPresent());
    assertThat(foundMember.get().getEmail()).isEqualTo(testMember.getEmail());
    assertThat(foundMember.get().getPassword()).isEqualTo(testMember.getPassword());
    assertThat(foundMember.get().getPhone()).isEqualTo(testMember.getPhone());
    assertThat(foundMember.get().getPinNumber()).isEqualTo(testMember.getPinNumber());
    assertThat(foundMember.get().getName()).isEqualTo(testMember.getName());
    assertThat(foundMember.get().getRole()).isEqualTo(testMember.getRole());
    assertThat(foundMember.get().getIsActivated()).isEqualTo(testMember.getIsActivated());
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 조회 시 회원 정보를 반환하지 않는 테스트")
  void testFindByEmail_NotFound() {
    // when
    Optional<Member> foundMember = memberRepository.findByEmail("test@naver.com");

    // then
    assertFalse(foundMember.isPresent());
  }
}
