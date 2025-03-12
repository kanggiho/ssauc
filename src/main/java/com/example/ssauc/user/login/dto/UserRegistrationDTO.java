package com.example.ssauc.user.login.dto;

import lombok.Data;

/**
 * 회원가입에 필요한 필드들을 모은 DTO
 */
@Data
public class UserRegistrationDTO {
    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String smsCode;

    // Firebase에서 받은 Phone 인증 토큰 (IdToken)
    private String firebaseToken;
}
