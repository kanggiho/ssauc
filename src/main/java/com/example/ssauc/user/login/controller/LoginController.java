package com.example.ssauc.user.login.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        try {
            userService.register(username, password);
            return "redirect:/login?success=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "success", required = false) String success,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "로그인 실패! 아이디와 비밀번호를 확인하세요.");
        }
        if (success != null) {
            model.addAttribute("successMessage", "회원가입 성공! 로그인해 주세요.");
        }
        return "login/login"; // login.html이 templates/login 폴더에 있기 때문에 수정
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        if (userService.login(username, password, session)) {
            return "redirect:/"; // 로그인 성공 시 index.html로 이동
        } else {
            return "redirect:/login?error=true";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
