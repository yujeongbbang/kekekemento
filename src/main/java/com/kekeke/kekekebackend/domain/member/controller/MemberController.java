package com.kekeke.kekekebackend.domain.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
	//private final MemberService memberService;

	@PermitAll
	@GetMapping("/sign-up/check-duplicate-nickname")
	public ResponseEntity<Boolean> checkDuplicateNickname(
			@RequestParam String nickname)
	{
		return ResponseEntity.ok();
	}

	@PermitAll
	@GetMapping("/sign-up/check-duplicate-username")
	public ResponseEntity<Boolean> checkDuplicateUsername(
			@RequestParam String username)
	{
		return ResponseEntity.ok();
	}

	@PermitAll
	@PostMapping("/sign-up")
	public ResponseEntity<Boolean> signUp()
	{
		return ResponseEntity.ok();
	}
}
