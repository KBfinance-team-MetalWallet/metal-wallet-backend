package com.kb.wallet.member.controller;

import com.kb.wallet.jwt.JwtFilter;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.dto.request.LoginMemberRequest;
import com.kb.wallet.member.dto.request.RegisterMemberRequest;
import com.kb.wallet.member.dto.response.RegisterMemberResponse;
import com.kb.wallet.member.service.MemberService;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Lazy
    private final TokenProvider tokenProvider;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<RegisterMemberResponse> registerMember(
            @RequestBody @Valid RegisterMemberRequest request) {
        log.info("Registering member: {}", request);
        RegisterMemberResponse response = memberService.registerMember(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> loginMember(
            @RequestBody @Valid LoginMemberRequest request) {
        log.info("Login member: {}", request);

        HashMap<String, Object> map = new HashMap<>();
        Member member = memberService.getMemberByEmail(request.getEmail());

        // 사용자 체크
        if (member == null) {
            map.put("result", "fail");
            map.put("email", request.getEmail());
            map.put("message", "사용자를 찾을 수 없습니다.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        // 패스워드 체크
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            map.put("result", "fail");
            map.put("email", request.getEmail());
            map.put("message", "패스워드가 일치하지 않습니다");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        String accessToken = tokenProvider.createToken(member.getEmail(), member.getRole().name());

        map.put("result", "success");
        map.put("accessToken", accessToken);

        // 헤더에 정보 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
        return new ResponseEntity<>(map, httpHeaders, HttpStatus.OK);
    }
}