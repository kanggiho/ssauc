package com.example.ssauc.user.main.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.main.service.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final HttpSession httpSession;

//    @PostMapping("/{productId}")
//    public ResponseEntity<?> toggleLike(@PathVariable Long productId) {
//        Users user = (Users) httpSession.getAttribute("user");
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//        }
//
////        boolean liked = likeService.toggleLike(user.getId(), productId);
////        return ResponseEntity.ok(Collections.singletonMap("liked", liked));
////    }
}