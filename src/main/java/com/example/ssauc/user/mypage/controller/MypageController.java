package com.example.ssauc.user.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage() {
        return "mypage/mypage"; // templates/mypage/mypage.html 파일을 렌더링
    }

    @GetMapping("/cash")  // SSAUC 머니 페이지로 이동
    public String cashPage() {
        return "mypage/cash";
    }

    @GetMapping("/evaluate")  // 리뷰 작성 페이지로 이동
    public String evaluatePage() {
        return "mypage/evaluate";
    }

    @GetMapping("/evaluation")  // 리뷰 내역 페이지로 이동
    public String evaluationPage() {
        return "mypage/evaluation";
    }

    @GetMapping("/evaluated")  // 리뷰 상세 페이지로 이동
    public String evaluatedPage() {
        return "mypage/evaluated";
    }

    @GetMapping("/ban")  // 리뷰 페이지로 이동
    public String banPage() {
        return "mypage/ban";
    }

    @GetMapping("/reported")  // 리뷰 내역 페이지로 이동
    public String reportedPage() {
        return "mypage/reported";
    }

    @GetMapping("/sell")  // 판매 현황 리스트 페이지로 이동
    public String sellPage() {
        return "mypage/sell";
    }

    @GetMapping("/sold")  // 판매 현황 상세 페이지로 이동
    public String soldPage() {
        return "mypage/sold";
    }

    @GetMapping("/buy")  // 구매 현황 리스트 페이지로 이동
    public String buyPage() {
        return "mypage/buy";
    }

    @GetMapping("/bought")  // 구매 현황 리스트 페이지로 이동
    public String boughtPage() {
        return "mypage/bought";
    }

}
