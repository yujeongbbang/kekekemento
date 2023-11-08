package com.kekeke.kekekebackend.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kekeke.kekekebackend.common.exception.BusinessException;
import com.kekeke.kekekebackend.common.exception.ErrorCode;
import com.kekeke.kekekebackend.common.jwt.Jwt;
import com.kekeke.kekekebackend.domain.member.model.dto.request.LoginRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.SignUpRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.TokenRefreshRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.UpdateRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.response.MemberResponse;
import com.kekeke.kekekebackend.domain.member.model.dto.response.TokenResponse;
import com.kekeke.kekekebackend.domain.member.model.entity.Member;
import com.kekeke.kekekebackend.domain.member.model.vo.MemberAuthority;
import com.kekeke.kekekebackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


	public boolean checkDuplicateNickname(String nickname)
	{
		return memberRepository.existsByNickname(nickname);
	}

	public boolean checkDuplicateUsername(String username)
	{
		return memberRepository.existsByUsername(username);
	}

	@Transactional
	public void signUp(SignUpRequest req)
	{
		memberRepository.save(
				Member.builder()
						.nickname(req.getNickname())
						.username(req.getUsername())
						.passwordHashed(passwordEncoder.encode(req.getPassword()))
						.phoneNumber(req.getPhoneNumber())
						.memberAuthority(MemberAuthority.USER)
						.build());
	}

	public TokenResponse login(LoginRequest req)
	{
		var member = memberRepository.findByUsername(req.getUsername());
		if (member.isPresent() == false)
		{
			throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
		}

		if (passwordEncoder.matches(req.getPassword(), member.get().getPasswordHashed()) == false)
		{
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		return publishToken(member.get());
	}


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
	public MemberResponse get(Long memberId)
	{
		var member = getById(memberId);

		return MemberResponse.mapFromMember(member);
	}

	@Transactional
	public void update(Long memberId, UpdateRequest req)
	{
		var member = getById(memberId);

		member.setNickname(req.getNickname());
		member.setPhoneNumber(req.getPhoneNumber());
		member.setPasswordHashed(passwordEncoder.encode(req.getPassword()));
		member.setProfilePhoto(req.getProfilePhoto());
	}

	@Transactional
	public void delete(Long memberId)
	{
		memberRepository.deleteById(memberId);
	}

	@Transactional
	public void updateFcmToken(Long memberId, String fcmToken)
	{
		var member = getById(memberId);
		member.setFcmToken(fcmToken);
	}
}
