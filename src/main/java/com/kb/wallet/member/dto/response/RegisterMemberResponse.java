package com.kb.wallet.member.dto.response;

import lombok.Getter;

@Getter
public class RegisterMemberResponse {

    private long id;
    private String email;
    private String name;

    public RegisterMemberResponse(long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
