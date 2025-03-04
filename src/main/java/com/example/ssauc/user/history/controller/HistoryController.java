package com.example.ssauc.user.history.controller;

import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.history.repository.BanHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("history")
public class HistoryController {

    private final BanHistoryService banHistoryService;

    public HistoryController(BanHistoryService banHistoryService) {
        this.banHistoryService = banHistoryService;
    }

//    @GetMapping("history")
//    public String history() {
//        return "/history/history";
//    }

    //    @GetMapping("/ban")
//    public String banPage() {
//        return "/history/ban";
//    }

    // 차단 내역 페이지: 로그인한 사용자의 차단 내역 조회
    @GetMapping("/ban")
    public String banPage(Model model, HttpSession session,
                          @RequestParam(defaultValue = "1") int page) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        // 페이지 번호는 0부터 시작하므로 (page - 1)로 변환
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BanHistoryDto> banPage = banHistoryService.getBanListForUser(userId, pageable);
        model.addAttribute("banList", banPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", banPage.getTotalPages());
        return "history/ban";
    }

    // 차단 해제 요청 처리
    @PostMapping("/ban/unban")
    public String unbanUser(@RequestParam("banId") Long banId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        banHistoryService.unbanUser(banId, userId);
        return "redirect:/history/ban";
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
