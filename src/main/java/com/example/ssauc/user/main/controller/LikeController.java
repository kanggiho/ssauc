package com.example.ssauc.user.main.controller;

import com.example.ssauc.user.main.service.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> toggleLike(HttpSession session, @RequestBody Map<String, Object> requestData) {

        System.out.println("==============여기 어떻게 받나요? ========");
        System.out.println(requestData);

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        Long productId = Long.parseLong(requestData.get("productId").toString());
        boolean isLiked = likeService.toggleLike(userId, productId);

        System.out.println("====================================");
        System.out.println(productId);
        System.out.println(userId);
        System.out.println(isLiked);
        System.out.println("====================================");

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "liked", isLiked,
                "message", isLiked ? "좋아요 추가됨" : "좋아요 취소됨"));
    }
}