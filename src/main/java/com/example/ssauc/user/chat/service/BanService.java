package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.chat.repository.BanRepository;
import com.example.ssauc.user.login.entity.Users;


import com.example.ssauc.user.login.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
@RequiredArgsConstructor
public class BanService {

    private final BanRepository banRepository;
    private final UsersRepository usersRepository;

//    //특정사용자를 차단
//    public Ban blockUser(Users user, Users blockedUser) {
//        // 이미 차단했는지 확인
//        if (banRepository.findByUserAndBlockedUser(user, blockedUser).isPresent()) {
//            throw new RuntimeException("이미 차단한 사용자입니다.");
//        }
//
//
//        Ban ban = new Ban(user, blockedUser, LocalDateTime.now());
//        return banRepository.save(ban);
//    }
//
//
//    /**
//     * 특정 사용자가 다른 사용자를 차단했는지 여부
//     * @param user 차단한 유저
//     * @param blockedUser 차단된 유저
//     * @return true/false
//     */
//    public boolean isBlocked(Users user, Users blockedUser) {
//        return banRepository.findByUserAndBlockedUser(user, blockedUser).isPresent();
//    }


    public void banUser(Long userId, Long blockedUserId) {
        // 차단하는 사용자와 차단 당하는 사용자 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("차단하는 사용자를 찾을 수 없습니다."));
        Users blockedUser = usersRepository.findById(blockedUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 당하는 사용자를 찾을 수 없습니다."));



        // 이미 차단되어 있는지 확인 (status 1: 활성 상태)
        boolean alreadyBanned = banRepository.existsByUserAndBlockedUserAndStatus(user, blockedUser, 1);
        if (alreadyBanned) {
            throw new IllegalStateException("이미 차단된 사용자입니다.");
        }

        // Ban 엔티티 생성
        Ban ban = Ban.builder()
                .user(user)
                .blockedUser(blockedUser)
                .blockedAt(LocalDateTime.now())
                .status(1)  // 1: 차단 상태 (활성화)
                .build();

        // ban 테이블에 저장
        banRepository.save(ban);
    }



}








