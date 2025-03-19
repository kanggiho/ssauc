package com.example.ssauc.user.login.controller;


import com.example.ssauc.user.login.dto.UserRegistrationDTO;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 회원가입/중복검사 REST API
 * signup.js에서 fetch()로 호출
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UsersRepository userRepository;

    /**
     * (1) 회원가입 처리
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDTO dto) {
        String result = userService.register(dto);
        if ("회원가입 성공".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * (2) 이메일 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("유효한 이메일 주소를 입력하세요.");
        }
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && "active".equalsIgnoreCase(userOpt.get().getStatus())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }
        // 만약 존재하지만 inactive 상태라면 사용 가능하도록 처리
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    /**
     * (3) 닉네임 중복 확인
     */
    @GetMapping("/check-username")
    public ResponseEntity<String> checkUsername(@RequestParam("username") String username) {
        if (username == null || username.trim().length() < 2) {
            return ResponseEntity.badRequest().body("닉네임은 최소 2글자 이상이어야 합니다.");
        }
        if (userRepository.existsByUserName(username)) {
            return ResponseEntity.badRequest().body("이미 사용 중인 닉네임입니다.");
        }
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }
}
