package com.kekeke.kekekebackend.domain.member.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberAuthority {
	USER("ROLE_USER");

	private final String role;
}
