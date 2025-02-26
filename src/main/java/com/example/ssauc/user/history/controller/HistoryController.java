package com.example.ssauc.user.history.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("history")
public class HistoryController {

//    @GetMapping("history")
//    public String history() {
//        return "/history/history";
//    }

    @GetMapping("/ban")  // 리뷰 페이지로 이동
    public String banPage() {
        return "/history/ban";
    }

    @GetMapping("/reported")  // 리뷰 내역 페이지로 이동
    public String reportedPage() {
        return "/history/reported";
    }

    @GetMapping("/sell")  // 판매 현황 리스트 페이지로 이동
    public String sellPage() {
        return "/history/sell";
    }

    @GetMapping("/sold")  // 판매 현황 상세 페이지로 이동
    public String soldPage() {
        return "/history/sold";
    }

    @GetMapping("/buy")  // 구매 현황 리스트 페이지로 이동
    public String buyPage() {
        return "/history/buy";
    }

    @GetMapping("/bought")  // 구매 현황 리스트 페이지로 이동
    public String boughtPage() {
        return "/history/bought";
    }
}
