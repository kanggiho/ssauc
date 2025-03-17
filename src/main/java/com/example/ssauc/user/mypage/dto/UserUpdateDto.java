package com.example.ssauc.user.mypage.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String userName;
    private String password;
    private String confirmPassword;
    private String phone;
    // 개별 주소 필드
    private String zipcode;
    private String address;
    private String addressDetail;
    // 프로필 이미지 URL (S3 업로드 후 받아온 URL)
    private String profileImage;
}
