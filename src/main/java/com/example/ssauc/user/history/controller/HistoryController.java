package com.example.ssauc.user.history.controller;

import com.example.ssauc.user.bid.dto.CarouselImage;
import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.history.dto.SellHistoryCompletedDto;
import com.example.ssauc.user.history.dto.SellHistoryOngoingDto;
import com.example.ssauc.user.history.dto.SoldDetailDto;
import com.example.ssauc.user.history.service.HistoryService;
import com.example.ssauc.user.login.entity.Users;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    // ===================== 차단 관리 =====================
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
        Page<BanHistoryDto> banPage = historyService.getBanListForUser(latestUser.getUserId(), pageable);
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

        historyService.unbanUser(banId, latestUser.getUserId());
        return "redirect:/history/ban";
    }

    // ===================== 리뷰 관리 =====================
    @GetMapping("/reported")  // 리뷰 내역 페이지로 이동
    public String reportedPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/history/reported";
    }

    // ===================== 판매 내역 =====================
    @GetMapping("/sell")  // 판매 현황 리스트 페이지로 이동
    public String sellPage(@RequestParam(value = "filter", required = false, defaultValue = "ongoing") String filter,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);

        int pageSize = 10;
        if ("ongoing".equals(filter)) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<SellHistoryOngoingDto> ongoingPageData = historyService.getOngoingSellHistoryPage(latestUser, pageable);
            model.addAttribute("list", ongoingPageData.getContent());
            model.addAttribute("totalPages", ongoingPageData.getTotalPages());
        } else if ("ended".equals(filter)) {
            // 판매 마감 리스트: 판매중 상태에서 (경매 마감 시간 + 30분) < 현재 시간인 상품
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<SellHistoryOngoingDto> endedPageData = historyService.getEndedSellHistoryPage(latestUser, pageable);
            model.addAttribute("list", endedPageData.getContent());
            model.addAttribute("totalPages", endedPageData.getTotalPages());
        } else if ("completed".equals(filter)) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<SellHistoryCompletedDto> completedPageData = historyService.getCompletedSellHistoryPage(latestUser, pageable);
            model.addAttribute("list", completedPageData.getContent());
            model.addAttribute("totalPages", completedPageData.getTotalPages());
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("filter", filter);
        return "/history/sell";
    }

    @GetMapping("/sold")  // 판매 현황 상세 페이지로 이동
    public String soldPage(@RequestParam("id") Long productId, HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = historyService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);

        // 상품 및 주문 상세 정보 조회 (SoldDetailDto를 이용)
        SoldDetailDto soldDetail = historyService.getSoldDetailByProductId(productId);
        model.addAttribute("soldDetail", soldDetail);

        // imageUrl을 쉼표로 분리하여 CarouselImage 리스트 생성 (BidService와 유사한 로직)
        String imageUrlStr = soldDetail.getImageUrl();  // SoldDetailDto의 imageUrl 필드 (쉼표로 구분된 문자열)
        String[] urls = imageUrlStr.split(",");
        List<CarouselImage> carouselImages = new ArrayList<>();
        int i = 1;
        for (String url : urls) {
            CarouselImage image = new CarouselImage();
            image.setUrl(url.trim());
            image.setAlt("Slide " + i);
            i++;
            carouselImages.add(image);
        }
        model.addAttribute("carouselImages", carouselImages);

        return "/history/sold";
    }

    // ===================== 구매 내역 =====================
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
