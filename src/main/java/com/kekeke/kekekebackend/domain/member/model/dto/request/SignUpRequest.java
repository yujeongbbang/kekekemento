package com.kekeke.kekekebackend.domain.member.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequest
{
    private String nickname;
    private String username;
    private String password;
    private String phoneNumber;
}
