package com.example.ssauc.user.login.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UsersRepository userRepository;

    public UserService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 (비밀번호 암호화 없음)
    public void register(String username, String password) {
        if (userRepository.findByUserName(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // "USER" 역할 없이 username과 password만 저장
        Users user = new Users(username, password);
        userRepository.save(user);
    }

    // 로그인 (세션 저장)
    public boolean login(String username, String password, HttpSession session) {
        Optional<Users> userOptional = userRepository.findByUserName(username);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // 비밀번호 일치 확인 (평문 비교)
            if (password.equals(user.getPassword())) {
                session.setAttribute("user", user); // 세션에 저장
                return true;
            }
        }
        return false;
    }

    // 로그아웃
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
