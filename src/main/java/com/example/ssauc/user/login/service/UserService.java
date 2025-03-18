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

    public Users getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public String register(UserRegistrationDTO dto) {
        // 이메일, 닉네임, 비밀번호 유효성 검사
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "유효한 이메일 입력";
        }
        Optional<Users> existingUserOpt = userRepository.findByEmail(dto.getEmail());
        if (existingUserOpt.isPresent()) {
            Users existingUser = existingUserOpt.get();
            if ("active".equalsIgnoreCase(existingUser.getStatus())) {
                return "이미 가입된 이메일";
            } else {
                // inactive 상태이면 기존 정보를 업데이트하여 재가입(재활성화) 시킵니다.
                existingUser.setUserName(dto.getUserName());
                existingUser.setPhone(dto.getPhone());
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
                String fullLocation = dto.getZipcode() + " " + dto.getAddress() + " " + dto.getAddressDetail();
                existingUser.setLocation(fullLocation);
                existingUser.setUpdatedAt(LocalDateTime.now());
                // inactive에서 active로 변경
                existingUser.setStatus("active");
                userRepository.save(existingUser);
                return "회원가입 성공";
            }
        }
        Optional<Users> existingUserByNicknameOpt = userRepository.findByUserName(dto.getUserName());
        if (existingUserByNicknameOpt.isPresent()) {
            Users existingUserByNickname = existingUserByNicknameOpt.get();
            // 닉네임이 active 상태이거나, inactive 상태지만 본인 이메일이 아니면 중복 처리
            if ("active".equalsIgnoreCase(existingUserByNickname.getStatus()) ||
                    !existingUserByNickname.getEmail().equals(dto.getEmail())) {
                return "이미 사용 중인 닉네임";
            }
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
        // 주소 정보 결합: 우편번호, 기본주소, 상세주소
        String fullLocation = dto.getZipcode() + " " + dto.getAddress() + " " + dto.getAddressDetail();
        // DB 저장
        Users user = Users.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .location(fullLocation)
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

            // status가 active인지 확인 (대소문자 구분 없이 비교)
            if (!"active".equalsIgnoreCase(user.getStatus())) {
                log.warn("로그인 실패 - 사용자 상태가 active가 아님: {}", normalizedEmail);
                return Optional.empty();
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("비밀번호 일치함: {}", normalizedEmail);

                // lastLogin 업데이트
                user.updateLastLogin();
                userRepository.save(user);

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
