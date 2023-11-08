package com.kekeke.kekekebackend.domain.member.model.dto.response;

import com.kekeke.kekekebackend.domain.member.model.entity.Member;

public record MemberResponse(
		String username,
		String nickname,
		String phoneNumber,
		String profilePhoto
) {
	public static MemberResponse mapFromMember(Member member) {
		return new MemberResponse(
				member.getUsername(),
				member.getNickname(),
				member.getPhoneNumber(),
				member.getProfilePhoto()
		);
	}
}
