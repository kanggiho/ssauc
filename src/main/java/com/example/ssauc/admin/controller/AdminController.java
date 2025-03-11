package com.example.ssauc.admin.controller;

import com.example.ssauc.admin.service.AdminService;
import com.example.ssauc.admin.entity.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public String index() {
        return "/admin/admin";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");

        model.addAttribute("adminName", admin.getAdminName());


        return "/admin/adminhome";
    }

    @PostMapping("/home")
    public String home(@RequestParam("adminId") String adminId,
                       @RequestParam("adminPw") String adminPw,
                       Model model,
                       HttpSession session) {
        // 관리자 객체 받아오기 (검증 실패 시 null 반환)
        Admin admin = adminService.checkAddress(adminId, adminPw);
        if (admin == null) {
            // 검증 실패 시 에러 메시지를 model에 담고 로그인 폼 페이지를 다시 리턴
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "/admin/admin";
        }
        // 검증 성공 시 session에 admin 정보를 저장하고, home 페이지로 리다이렉트
        session.setAttribute("admin", admin);
        return "redirect:/admin/home";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 세션 초기화
        return "redirect:/admin";  // 로그인 화면(예: /admin)으로 리다이렉트
    }
}
