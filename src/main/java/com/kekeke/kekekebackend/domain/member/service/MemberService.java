package com.kekeke.kekekebackend.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.kekeke.kekekebackend.common.exception.BusinessException;
import com.kekeke.kekekebackend.common.exception.ErrorCode;
import com.kekeke.kekekebackend.common.jwt.Jwt;
import com.kekeke.kekekebackend.domain.member.model.entity.Member;
import com.kekeke.kekekebackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	private final Jwt jwt;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final ObjectMapper objectMapper = new ObjectMapper();


	@Transactional
	public Member getById(Long memberId)
	{
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	@Transactional
	public TokenResponse publishToken(Member member)
	{
		TokenResponse tokenResponse = jwt.generateAllToken(
				Jwt.Claims.from(
						member.getId(),
						new String[] {
								member.getMemberAuthority().getRole()
						})
		);

		member.setRefreshToken(tokenResponse.refreshToken());

		return tokenResponse;
	}

	@Transactional
	public void logout(Long memberId)
	{
		var member = getById(memberId);
		member.setRefreshToken("");
	}

	@Transactional
	public TokenResponse reissueToken(TokenRefreshRequest tokenRefreshRequest) {
		var member = memberRepository.findByRefreshToken(tokenRefreshRequest.refreshToken());
		if (member.isPresent() == false) {
			throw new AccessDeniedException("refresh token 이 만료되었습니다.");
		}

		Long memberId;
		String[] roles;

		try {
			Jwt.Claims claims = jwt.verify(member.get().getRefreshToken());
			memberId = claims.getMemberId();
			roles = claims.getRoles();
		} catch (Exception e) {
			log.warn("Jwt 처리중 문제가 발생하였습니다 : {}", e.getMessage());
			throw e;
		}
		TokenResponse tokenResponse = jwt.generateAllToken(Jwt.Claims.from(memberId, roles));

		member.get().setRefreshToken(tokenResponse.refreshToken());

		return tokenResponse;
	}

	@Transactional
	public void deleteMember(Long memberId)
	{
		memberRepository.deleteById(memberId);
	}


	@Transactional
	public void updateFcmToken(Long memberId, String fcmToken)
	{
		var member = getById(memberId);
		member.setFcmToken(fcmToken);
	}


	private LoginResponse getLoginResponse(Member member)
	{
		var tokens = publishToken(member);
		if (member.getProfileName() == null ||
				member.getProfileName().isEmpty())
		{
			return new LoginResponse(tokens, true);
		}
		else
		{
			return new LoginResponse(tokens, false);
		}
	}
}
