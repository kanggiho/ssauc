package com.example.ssauc.user.login.service;

import com.example.ssauc.user.login.dto.LoginResponseDTO;
import com.example.ssauc.user.login.dto.UserRegistrationDTO;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.login.util.JwtUtil;
import com.example.ssauc.user.login.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String register(UserRegistrationDTO dto) {
        // 이메일, 닉네임, 비밀번호 유효성 검사
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "유효한 이메일 입력";
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "이미 가입된 이메일";
        }
        if (userRepository.existsByUserName(dto.getUserName())) {
            return "이미 존재하는 닉네임";
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            return "이미 사용 중인 전화번호";
        }
        // 비밀번호 복잡성 검사
        if (!PasswordValidator.isValid(dto.getPassword())) {
            return "비밀번호가 형식에 맞지 않음";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return "비밀번호 불일치";
        }

        // DB 저장
        Users user = Users.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return "회원가입 성공";
    }

    public Optional<LoginResponseDTO> login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        log.info("로그인 시도: {}", normalizedEmail);
        Optional<Users> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            log.info("사용자 조회 성공: {}", normalizedEmail);
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("비밀번호 일치함: {}", normalizedEmail);
                String accessToken = jwtUtil.generateAccessToken(normalizedEmail);
                String refreshToken = jwtUtil.generateRefreshToken(normalizedEmail);
                log.info("생성된 Access Token: {}", accessToken);
                log.info("생성된 Refresh Token: {}", refreshToken);
                refreshTokenService.saveRefreshToken(normalizedEmail, refreshToken);
                return Optional.of(new LoginResponseDTO(accessToken, refreshToken));
            } else {
                log.warn("비밀번호 불일치: 입력한 값={} / DB 값={}", password, user.getPassword());
            }
        } else {
            log.warn("사용자 조회 실패: {}", normalizedEmail);
        }
        return Optional.empty();
    }
}
