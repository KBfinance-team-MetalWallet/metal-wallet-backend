package com.kb.wallet.member.repository;

import com.kb.wallet.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Member> getByEmail(String email);
}
