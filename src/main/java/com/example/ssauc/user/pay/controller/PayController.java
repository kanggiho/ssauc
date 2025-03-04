package com.example.ssauc.user.pay.controller;

import com.example.ssauc.user.bid.dto.OrderRequestDto;
import com.example.ssauc.user.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pay")
public class PayController {

    @Autowired
    PaymentService paymentService;

    // POST 요청 처리: 결제 처리 로직 수행
    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody OrderRequestDto orderRequestDto) {
        // 결제 처리 로직 실행
        boolean success = paymentService.processPayment(orderRequestDto);
        if (success) {
            // 처리 성공 시 HTTP 200 응답 반환
            return ResponseEntity.ok().build();
        } else {
            // 처리 실패 시 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결제 처리 중 오류가 발생했습니다.");
        }
    }

    // GET 요청 처리: 결제 완료 후 보여줄 페이지 반환
    @GetMapping("/pay")
    public String pay(Model model) {



        // 필요한 데이터를 model에 추가하고, 뷰 이름 반환
        return "pay/pay";
    }
}
