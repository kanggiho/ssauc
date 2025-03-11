package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.chat.repository.BanRepository;
import com.example.ssauc.user.login.entity.Users;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
public class BanService {

    private final BanRepository banRepository;

    public BanService(BanRepository banRepository) {
        this.banRepository = banRepository;
    }


    //특정사용자를 차단
    public Ban blockUser(Users user, Users blockedUser) {
        // 이미 차단했는지 확인
        if (banRepository.findByUserAndBlockedUser(user, blockedUser).isPresent()) {
            throw new RuntimeException("이미 차단한 사용자입니다.");
        }


        Ban ban = new Ban(user, blockedUser, LocalDateTime.now());
        return banRepository.save(ban);
    }


    /**
     * 특정 사용자가 다른 사용자를 차단했는지 여부
     * @param user 차단한 유저
     * @param blockedUser 차단된 유저
     * @return true/false
     */
    public boolean isBlocked(Users user, Users blockedUser) {
        return banRepository.findByUserAndBlockedUser(user, blockedUser).isPresent();
    }



}








