package com.example.ssauc.user.main.controller;

import com.example.ssauc.user.main.service.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> toggleLike(HttpSession session, @RequestBody Map<String, Object> requestData) {

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        Long productId = Long.parseLong(requestData.get("productId").toString());
        boolean isLiked = likeService.toggleLike(userId, productId);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "liked", isLiked,
                "message", isLiked ? "좋아요 추가됨" : "좋아요 취소됨"));
    }
}