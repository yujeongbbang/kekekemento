package com.kekeke.kekekebackend.domain.member.controller;


import com.kekeke.kekekebackend.domain.member.model.dto.request.LoginRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.SignUpRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.TokenRefreshRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.request.UpdateRequest;
import com.kekeke.kekekebackend.domain.member.model.dto.response.MemberResponse;
import com.kekeke.kekekebackend.domain.member.model.dto.response.TokenResponse;
import com.kekeke.kekekebackend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

import static com.kekeke.kekekebackend.common.util.AuthenticationUtil.getMemberId;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	@PermitAll
	@GetMapping("/sign-up/check-duplicate-nickname")
	public ResponseEntity<Boolean> checkDuplicateNickname(
			@RequestParam String nickname)
	{
		return ResponseEntity.ok(memberService.checkDuplicateNickname(nickname));
	}

	@PermitAll
	@GetMapping("/sign-up/check-duplicate-username")
	public ResponseEntity<Boolean> checkDuplicateUsername(
			@RequestParam String username)
	{
		return ResponseEntity.ok(memberService.checkDuplicateUsername(username));
	}

	@PermitAll
	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUp(@RequestBody SignUpRequest req)
	{
		memberService.signUp(req);

		return ResponseEntity.noContent().build();
	}

	@PermitAll
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req)
	{
		return ResponseEntity.ok(memberService.login(req));
	}

	@PermitAll
	@PostMapping("/token/refresh")
	public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRefreshRequest req)
	{
		return ResponseEntity.ok(memberService.reissueToken(req)); // 200
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/member")
	public ResponseEntity<MemberResponse> getMemberInfo()
	{
		return ResponseEntity.ok(memberService.get(getMemberId()));
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/member")
	public ResponseEntity<Void> updateMember(@RequestBody UpdateRequest req)
	{
		memberService.update(getMemberId(), req);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/member")
	public ResponseEntity<Void> deleteMember()
	{
		memberService.delete(getMemberId());

		return ResponseEntity.noContent().build();
	}
}
