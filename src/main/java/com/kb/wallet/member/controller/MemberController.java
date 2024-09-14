package com.kb.wallet.member.controller;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberServiceImpl memberService;

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    /**
     * repository use
     * Optional<Member> member = memberService.getMemberById(id);
     *
     * mapper use
     * Optional<Member> member = memberService.getMemberById2(id);
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") Long id) {
        log.info("Finding member by id: {}", id);

        Optional<Member> member = memberService.getMemberById(id);

        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        log.info("Inserting new member: {}", member);
        Member savedMember = memberService.createMember(member);
        return ResponseEntity.ok(savedMember);
    }
}