package com.kb.wallet.member.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.LoginMemberRequest;
import com.kb.wallet.member.dto.request.PinNumberVerificationRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.service.MemberService;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@Slf4j
@AllArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final AuthenticationManager authenticationManager;

  @Lazy
  private final TokenProvider tokenProvider;

  @PostMapping("/register")
  public ApiResponse<RegisterMemberResponse> registerMember(
    @RequestBody @Valid RegisterMemberRequest request) {
    RegisterMemberResponse response = memberService.registerMember(request);
    return ApiResponse.ok(response);
  }

  @PostMapping("/login")
  public ApiResponse<HashMap<String, Object>> loginMember(
    @RequestBody @Valid LoginMemberRequest request) {
    HashMap<String, Object> map = new HashMap<>();

    try {
      // 인증 요청을 만든다
      UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(),
          request.getPassword());

      // AuthenticationManager를 통해 인증을 시도한다
      Authentication authentication = authenticationManager.authenticate(authenticationToken);

      // 인증이 성공하면 JWT 토큰을 생성한다
      String role = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst() // 첫 번째 권한을 가져오기 (예: "ROLE_USER")
        .orElse("ROLE_USER");

      String accessToken = tokenProvider.createToken(authentication.getName(),
        role);

      map.put("result", "success");
      map.put("accessToken", accessToken);

      return ApiResponse.ok(map);

    } catch (AuthenticationException e) {
      map.put("result", "fail");
      map.put("message", "Login failed: " + e.getMessage());
      return ApiResponse.unauthorized(map);
    }
  }

  @PostMapping("/pin-number-verification")
  public ApiResponse<Void> checkPinNumber(
    @AuthenticationPrincipal Member member,
    @RequestBody @Valid PinNumberVerificationRequest verificationRequest) {
    memberService.checkPinNumber(member.getEmail(), verificationRequest);
    return ApiResponse.ok();
  }
}
