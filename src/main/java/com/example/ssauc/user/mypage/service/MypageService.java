package com.example.ssauc.user.mypage.service;

import com.example.ssauc.user.login.entity.Users;

public interface MypageService {
    Users getCurrentUser(Long userId);
}
