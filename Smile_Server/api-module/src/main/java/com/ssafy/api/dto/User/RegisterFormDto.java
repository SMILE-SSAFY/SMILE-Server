package com.ssafy.api.dto.User;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * 회원가입 Dto
 *
 * author @서재건
 */
@Builder
@Data
public class RegisterFormDto {
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;

    public RegisterFormDto(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

}
