package com.kekeke.kekekebackend.domain.member.repository;


import com.kekeke.kekekebackend.domain.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByRefreshToken(String refreshToken);

	Optional<Member> findByUsername(String email);

	boolean existsByUsername(String username);
	boolean existsByNickname(String nickname);

}
