package com.example.ssauc.user.cash.controller;

import com.example.ssauc.exception.PortoneVerificationException;
import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.ChargingDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.cash.service.CashService;
import com.example.ssauc.user.login.entity.Users;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Controller
@RequestMapping("cash")
public class CashController {

    @Autowired
    private CashService cashService;

    @GetMapping("/cash")
    public String cashPage(@RequestParam(value = "filter", required = false, defaultValue = "calculate") String filter,
                           @RequestParam(value = "startDate", required = false) String startDateStr,
                           @RequestParam(value = "endDate", required = false) String endDateStr,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           HttpSession session,
                           Model model) {

        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("filter", filter);

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if(startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()){
            // 변환 예시: 시작일은 00:00, 종료일은 23:59:59로 설정
            startDate = LocalDate.parse(startDateStr).atStartOfDay();
            endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // 날짜 필터가 있는 경우와 없는 경우를 분기해서 처리
        if ("charge".equals(filter)) {
            Page<ChargeDto> chargePage;
            if(startDate != null && endDate != null){
                chargePage = cashService.getChargesByUser(user, startDate, endDate, pageable);
            } else {
                chargePage = cashService.getChargesByUser(user, pageable);
            }
            model.addAttribute("chargeList", chargePage.getContent());
            model.addAttribute("totalPages", chargePage.getTotalPages());
            model.addAttribute("currentPage", page);
        } else if ("withdraw".equals(filter)) {
            Page<WithdrawDto> withdrawPage;
            if(startDate != null && endDate != null){
                withdrawPage = cashService.getWithdrawsByUser(user, startDate, endDate, pageable);
            } else {
                withdrawPage = cashService.getWithdrawsByUser(user, pageable);
            }
            model.addAttribute("withdrawList", withdrawPage.getContent());
            model.addAttribute("totalPages", withdrawPage.getTotalPages());
            model.addAttribute("currentPage", page);
        } else if ("calculate".equals(filter)) {
            Page<CalculateDto> calculatePage;
            if(startDate != null && endDate != null){
                calculatePage = cashService.getCalculateByUser(user, startDate, endDate, pageable);
            } else {
                calculatePage = cashService.getCalculateByUser(user, pageable);
            }
            model.addAttribute("calculateList", calculatePage.getContent());
            model.addAttribute("totalPages", calculatePage.getTotalPages());
            model.addAttribute("currentPage", page);
        }

        // startDateStr와 endDateStr도 모델에 담으면 페이지 링크에 유지할 수 있음
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);

        return "/cash/cash";
    }


    // 충전 옵션 정보 (예: 기본 충전 옵션)
    @GetMapping("/api/info")
    @ResponseBody
    public ChargingDto getChargingInfo() {
        // 필요에 따라 동적으로 변경 가능
        return ChargingDto.builder()
                .id("charge")
                .name("쏙머니 충전")
                .price(10000)
                .currency("KRW")
                .build();
    }

    // 결제 완료 처리 엔드포인트
    @PostMapping("/api/complete")
    @ResponseBody
    public ResponseEntity<?> completePayment(@RequestBody PaymentCompleteRequest request, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            // 결제 검증 및 완료 처리 (사용자 충전 기록 업데이트)
            Charge charge = cashService.verifyAndCompletePayment(request.getPaymentId(), request.getAmount(), user);
            return ResponseEntity.ok(new PaymentResponse("PAID", charge.getChargeId()));
        } catch (PortoneVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    // 웹훅 처리 엔드포인트 (선택사항)
//    @PostMapping("/api/webhook")
//    @ResponseBody
//    public ResponseEntity<?> handleWebhook(@RequestHeader("webhook-id") String webhookId,
//                                           @RequestHeader("webhook-timestamp") String webhookTimestamp,
//                                           @RequestHeader("webhook-signature") String webhookSignature,
//                                           @RequestBody String body) {
//        try {
//            cashService.handleWebhook(body, webhookId, webhookTimestamp, webhookSignature);
//            return ResponseEntity.ok("Webhook processed");
//        } catch (PortoneVerificationException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    // DTOs for 결제 완료 처리
    @Setter
    @Getter
    public static class PaymentCompleteRequest {
        private String paymentId;
        private Long amount;
    }

    @Getter
    public static class PaymentResponse {
        private String status;
        private Long chargeId;
        public PaymentResponse(String status, Long chargeId) {
            this.status = status;
            this.chargeId = chargeId;
        }
    }
}
