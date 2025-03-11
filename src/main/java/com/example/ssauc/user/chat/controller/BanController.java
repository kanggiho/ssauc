package com.example.ssauc.user.chat.controller;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.chat.service.BanService;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ban")
public class BanController {

    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }
        //차단 요청
    @PostMapping("/block")
    public String blockUser(@RequestParam("targetUserId") Long targetUserId) {
        // 실제로는 SecurityContextHolder 등에서 로그인 사용자 정보를 가져옴
        Users currentUser = new Users();
        currentUser.setUserId(1L); // 예: 로그인된 사용자 ID 1

        // 차단당할 사용자 객체 생성(또는 DB에서 조회)
        Users blockedUser = new Users();
        blockedUser.setUserId(targetUserId);

        Ban ban = banService.blockUser(currentUser, blockedUser);
        return "사용자 " + blockedUser.getUserId() + " 차단 완료 (banId=" + ban.getBanId() + ")";
    }



        //차단여부 확인
    @GetMapping("/isBlocked")
    public boolean isBlocked(@RequestParam("targetUserId") Long targetUserId) {
        Users currentUser = new Users();
        currentUser.setUserId(1L);

        Users blockedUser = new Users();
        blockedUser.setUserId(targetUserId);

        return banService.isBlocked(currentUser, blockedUser);
    }

}