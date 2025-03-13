package com.example.ssauc.user.mypage.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.mypage.dto.EvaluationDto;
import com.example.ssauc.user.mypage.dto.EvaluationPendingDto;
import com.example.ssauc.user.mypage.dto.EvaluationReviewDto;
import com.example.ssauc.user.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    private final MypageService mypageService;

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // DB에서 최신 사용자 정보를 조회
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userName = userDetails.getUsername();
        Users latestUser = mypageService.getCurrentUser(userName);
        model.addAttribute("user", latestUser);
        return "/mypage/mypage"; // templates/mypage/mypage.html
    }

    // 리뷰 현황 (작성 가능, 받은, 보낸)
    @GetMapping("/evaluation")
    public String evaluatePage(@RequestParam(value = "filter", required = false, defaultValue = "pending") String filter,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userName = userDetails.getUsername();
        Users latestUser = mypageService.getCurrentUser(userName);
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

    // 리뷰 작성 페이지
    @GetMapping("/evaluate")
    public String evaluationPage(@RequestParam("orderId") Long orderId,
                                 @RequestParam("productId") Long productId,
                                 @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userName = userDetails.getUsername();
        Users latestUser = mypageService.getCurrentUser(userName);
        model.addAttribute("user", latestUser);
        // 주문 정보를 기반으로 평가 데이터 준비 (상품명, 상대방 이름, 거래 유형 등)
        EvaluationDto evaluationDto = mypageService.getEvaluationData(orderId, latestUser);
        model.addAttribute("evaluationDto", evaluationDto);
        model.addAttribute("productName", evaluationDto.getProductName());
        model.addAttribute("otherUserName", evaluationDto.getOtherUserName());

        return "/mypage/evaluate";
    }

    // 리뷰 제출 처리 - JSON POST 요청을 받음
    @PostMapping("/evaluate/submit")
    @ResponseBody
    public ResponseEntity<?> submitEvaluation(@RequestBody EvaluationDto evaluationDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userName = userDetails.getUsername();
        Users latestUser = mypageService.getCurrentUser(userName);
        try {
            mypageService.submitEvaluation(evaluationDto, latestUser);
            return ResponseEntity.ok("평가가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("평가 제출에 실패했습니다: " + e.getMessage());
        }
    }

    // 리뷰 상세 페이지
    @GetMapping("/evaluated")
    public String evaluatedPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userName = userDetails.getUsername();
        Users latestUser = mypageService.getCurrentUser(userName);
        model.addAttribute("user", latestUser);
        return "/mypage/evaluated";
    }



}
