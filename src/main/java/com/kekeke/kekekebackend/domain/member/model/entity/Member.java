package com.kekeke.kekekebackend.domain.member.model.entity;


import com.kekeke.kekekebackend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Objects;

@Table(name = "member")
@Entity

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String nickname;

	@Column(unique = true)
	private String username; // id

	private String passwordHashed;

	private String phoneNumber;

	private String refreshToken;

	private String fcmToken;


	public Member(String nickname, String username, String passwordHashed, String phoneNumber) {
		this.nickname = nickname;
		this.username = username;
		this.passwordHashed = passwordHashed;
		this.phoneNumber = phoneNumber;
	}
}
