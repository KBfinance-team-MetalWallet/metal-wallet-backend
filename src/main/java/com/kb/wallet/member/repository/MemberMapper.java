package com.kb.wallet.member.repository;

import com.kb.wallet.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    List<Member> findAll();

    Member findById(Long id);
}