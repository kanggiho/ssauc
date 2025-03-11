package com.example.ssauc.user.mypage.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.mypage.dto.EvaluationPendingDto;
import com.example.ssauc.user.mypage.dto.EvaluationReviewDto;
import com.example.ssauc.user.mypage.service.MypageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // 리뷰 현황 (작성 가능, 받은, 보낸)
    @GetMapping("/evaluation")
    public String evaluatePage(@RequestParam(value = "filter", required = false, defaultValue = "pending") String filter,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);

        int pageSize = 10;
        if ("received".equals(filter)) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EvaluationReviewDto> receivedPageData = mypageService.getReceivedReviews(latestUser, pageable);
            model.addAttribute("reviewList", receivedPageData.getContent());
            model.addAttribute("totalPages", receivedPageData.getTotalPages());
        } else if ("written".equals(filter)) {
            // 판매 마감 리스트: 판매중 상태에서 (경매 마감 시간 + 30분) < 현재 시간인 상품
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EvaluationReviewDto> writtenPageData = mypageService.getWrittenReviews(latestUser, pageable);
            model.addAttribute("reviewList", writtenPageData.getContent());
            model.addAttribute("totalPages", writtenPageData.getTotalPages());
        } else if ("pending".equals(filter)) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
            Page<EvaluationPendingDto> pendingPageData = mypageService.getPendingReviews(latestUser, pageable);
            model.addAttribute("reviewList", pendingPageData.getContent());
            model.addAttribute("totalPages", pendingPageData.getTotalPages());
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("filter", filter);
        return "/mypage/evaluation";
    }

    @GetMapping("/evaluate")  // 리뷰 내역 페이지로 이동
    public String evaluationPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/evaluate";
    }

    @GetMapping("/evaluated")  // 리뷰 상세 페이지로 이동
    public String evaluatedPage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        Users latestUser = mypageService.getCurrentUser(user.getUserId());
        model.addAttribute("user", latestUser);
        return "/mypage/evaluated";
    }



}
