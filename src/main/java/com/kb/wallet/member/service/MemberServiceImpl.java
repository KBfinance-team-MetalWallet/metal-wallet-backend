package com.kb.wallet.member.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.repository.MemberMapper;
import com.kb.wallet.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Override
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> getMemberById2(Long id) {
        Member member = memberMapper.findById(id);
        return Optional.ofNullable(member);
    }

    @Override
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
}