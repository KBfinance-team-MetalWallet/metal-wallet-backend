package com.kb.wallet.test.controller;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.test.service.TestService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {

  private final TestService testService;

  @GetMapping("/members/{id}")
  public ResponseEntity<Member> getMemberById(@PathVariable("id") Long id) {
    log.info("Finding member by id: {}", id);
    Optional<Member> member = testService.getMemberById(id);
    return member.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/members")
  public ResponseEntity<Member> createMember(@RequestBody Member member) {
    log.info("Inserting new member: {}", member);
    Member savedMember = testService.createMember(member);
    return ResponseEntity.ok(savedMember);
  }
}
