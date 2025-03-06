package com.example.ssauc.user.history.controller;

import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.history.service.HistoryService;
import com.example.ssauc.user.login.entity.Users;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService banHistoryService;

    private final HistoryService historyService;

    // 차단 내역 페이지: 로그인한 사용자의 차단 내역 조회
    @GetMapping("/ban")
    public String banPage(@RequestParam(defaultValue = "1") int page,
                          HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        // 페이지 번호는 0부터 시작하므로 (page - 1)로 변환
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BanHistoryDto> banPage = banHistoryService.getBanListForUser(latestUser.getUserId(), pageable);
        model.addAttribute("banList", banPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", banPage.getTotalPages());
        return "history/ban";
    }

    // 차단 해제 요청 처리
    @PostMapping("/ban/unban")
    public String unbanUser(@RequestParam("banId") Long banId,
                            HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);

        banHistoryService.unbanUser(banId, latestUser.getUserId());
        return "redirect:/history/ban";
    }

    @GetMapping("/reported")  // 리뷰 내역 페이지로 이동
    public String reportedPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/reported";
    }

    @GetMapping("/sell")  // 판매 현황 리스트 페이지로 이동
    public String sellPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/sell";
    }

    @GetMapping("/sold")  // 판매 현황 상세 페이지로 이동
    public String soldPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/sold";
    }

    @GetMapping("/buy")  // 구매 현황 리스트 페이지로 이동
    public String buyPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/buy";
    }

    @GetMapping("/bought")  // 구매 현황 리스트 페이지로 이동
    public String boughtPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/bought";
    }
}
