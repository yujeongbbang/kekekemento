package com.kekeke.kekekebackend.domain.member.model.dto.response;

public record TokenResponse(
		String accessToken,
		String refreshToken
) {
}
