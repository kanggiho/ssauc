package com.example.ssauc.user.mypage.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.util.TokenExtractor;
import com.example.ssauc.user.mypage.dto.*;
import com.example.ssauc.user.mypage.service.MypageService;
import com.example.ssauc.user.mypage.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    private final MypageService mypageService;

    private final TokenExtractor tokenExtractor;
    private final UserProfileService userProfileService;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage(HttpServletRequest request, Model model) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = mypageService.getCurrentUser(user.getEmail());
        model.addAttribute("user", latestUser);
        return "/mypage/mypage";
    }

    // 프로필 수정 페이지 (개별 주소 필드 분리)
    @GetMapping("/profile-update")
    public String showProfileUpdate(HttpServletRequest request, Model model) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return "redirect:/login";
        }
        Users currentUser = userProfileService.getCurrentUser(user.getEmail());
        model.addAttribute("user", currentUser);
        // location 필드를 공백 기준으로 분리 (예: "우편번호 기본주소 상세주소")
        String[] addressParts = currentUser.getLocation() != null ? currentUser.getLocation().split(" ", 3) : new String[0];
        model.addAttribute("zipcode", addressParts.length > 0 ? addressParts[0] : "");
        model.addAttribute("address", addressParts.length > 1 ? addressParts[1] : "");
        model.addAttribute("addressDetail", addressParts.length > 2 ? addressParts[2] : "");
        return "mypage/profile-update";
    }

    // 2) 프로필 이미지 업로드 (S3)
    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        // 파일 크기 제한 (3MB)
        if(file.getSize() > 3 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("파일 크기는 3MB를 초과할 수 없습니다.");
        }
        // 이미지 여부 간단 체크
        if(!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다.");
        }

        try {
            // 고유 파일명 (timestamp_파일명)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);

            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }

    // 프로필 업데이트 처리 (AJAX JSON POST)
    @PostMapping("/profile-update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateDto dto, HttpServletRequest request) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            Users currentUser = userProfileService.getCurrentUser(user.getEmail());
            userProfileService.updateUserProfile(currentUser, dto);
            return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 리뷰 현황 (작성 가능, 받은, 보낸)
    @GetMapping("/evaluation")
    public String evaluatePage(@RequestParam(value = "filter", required = false, defaultValue = "pending") String filter,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               HttpServletRequest request, Model model) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = mypageService.getCurrentUser(user.getEmail());
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
                                 HttpServletRequest request, Model model) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = mypageService.getCurrentUser(user.getEmail());
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
    public ResponseEntity<?> submitEvaluation(@RequestBody EvaluationDto evaluationDto, HttpServletRequest request) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Users latestUser = mypageService.getCurrentUser(user.getEmail());
        try {
            mypageService.submitEvaluation(evaluationDto, latestUser);
            return ResponseEntity.ok("평가가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("평가 제출에 실패했습니다: " + e.getMessage());
        }
    }

    // 리뷰 상세 페이지 - reviewId를 통해 리뷰 상세 정보를 조회
    @GetMapping("/evaluated")
    public String evaluatedPage(@RequestParam("reviewId") Long reviewId,
                                HttpServletRequest request, Model model) {
        Users user = tokenExtractor.getUserFromToken(request);
        if (user == null) {
            return "redirect:/login";
        }
        Users latestUser = mypageService.getCurrentUser(user.getEmail());
        model.addAttribute("user", latestUser);

        EvaluatedDto reviewDto = mypageService.getReviewById(reviewId, latestUser.getUserId());
        model.addAttribute("review", reviewDto);

        // reviewType 결정: 현재 사용자가 리뷰 작성자이면 "written", 아니면 "received"
        String reviewType = "";
        if(latestUser.getUserName().equals(reviewDto.getReviewerName())) {
            reviewType = "written";
        } else {
            reviewType = "received";
        }
        model.addAttribute("reviewType", reviewType);

        return "/mypage/evaluated";
    }



}
