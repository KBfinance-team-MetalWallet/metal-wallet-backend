package com.kb.wallet.member.repository;

import com.kb.wallet.member.domain.Member;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

  List<Member> findAll();

  Member findById(Long id);
}