package com.example.ssauc.user.mypage.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MypageServiceImpl implements MypageService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Users getCurrentUser() {
        // 예시: 현재 로그인한 유저 정보를 Security Context나 세션에서 가져오는 로직을 구현해야 함.
        // 여기서는 단순하게 userId 1번 유저를 조회하는 예시입니다.
        return usersRepository.findById(1L).orElse(null);
    }
}
