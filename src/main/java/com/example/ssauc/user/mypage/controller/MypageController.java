package com.example.ssauc.user.mypage.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.mypage.service.MypageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    private final MypageService mypageService;

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage(HttpSession session, Model model) {
        // DB에서 최신 사용자 정보를 조회
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/mypage"; // templates/mypage/mypage.html
    }

    @GetMapping("/evaluate")  // 리뷰 작성 페이지로 이동
    public String evaluatePage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/evaluate";
    }

    @GetMapping("/evaluation")  // 리뷰 내역 페이지로 이동
    public String evaluationPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/evaluation";
    }

    @GetMapping("/evaluated")  // 리뷰 상세 페이지로 이동
    public String evaluatedPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/evaluated";
    }



}
