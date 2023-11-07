package com.kekeke.kekekebackend.domain.member.repository;


import com.kekeke.kekekebackend.domain.member.model.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsBySocialPlatformTypeAndSocialAccountUid(SocialPlatformType socialPlatformType, String socialAccountUid);
	Optional<Member> findBySocialPlatformTypeAndSocialAccountUid(SocialPlatformType socialPlatformType, String socialAccountUid);
	Optional<Member> findByRefreshToken(String refreshToken);
	boolean existsByEmail(String email);

	Optional<Member> findByUsernameAndPhoneNumber(String username, String phoneNumber);
	Optional<Member> findByUsername(String email);

}
