package com.example.ssauc.user.mypage.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UsersRepository usersRepository;

    // 세션에서 전달된 userId를 이용하여 DB에서 최신 사용자 정보를 조회합니다.
    @Override
    public Users getCurrentUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
    }
}
