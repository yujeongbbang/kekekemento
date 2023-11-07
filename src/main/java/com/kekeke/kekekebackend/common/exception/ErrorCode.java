package com.kekeke.kekekebackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
	DUPLICATED(HttpStatus.CONFLICT, "중복된 데이터입니다."),
	EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않는 이메일 입니다.");
	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
