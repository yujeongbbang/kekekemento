package com.kekeke.kekekebackend.domain.member.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateRequest
{
    private String nickname;
    private String password;
    private String phoneNumber;
    private String profilePhoto;
}
