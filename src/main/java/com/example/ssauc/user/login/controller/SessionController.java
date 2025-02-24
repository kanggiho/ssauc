package com.example.ssauc.user.login.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.entity.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SessionController {

    @GetMapping("/checkSession")
    @ResponseBody
    public Users checkSession(HttpSession session) {
        return (Users) session.getAttribute("user");
    }
}
