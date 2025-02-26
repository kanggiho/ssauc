package com.example.ssauc.user.mypage.controller;

import com.example.ssauc.user.mypage.service.MypageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    @Autowired
    private MypageService mypageService;

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage() {
        return "mypage/mypage"; // templates/mypage/mypage.html 파일을 렌더링
    }

    @GetMapping("/evaluate")  // 리뷰 작성 페이지로 이동
    public String evaluatePage() {
        return "/mypage/evaluate";
    }

    @GetMapping("/evaluation")  // 리뷰 내역 페이지로 이동
    public String evaluationPage() {
        return "/mypage/evaluation";
    }

    @GetMapping("/evaluated")  // 리뷰 상세 페이지로 이동
    public String evaluatedPage() {
        return "/mypage/evaluated";
    }



}
