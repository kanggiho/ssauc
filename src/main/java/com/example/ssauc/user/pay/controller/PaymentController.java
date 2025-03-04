package com.example.ssauc.user.pay.controller;

import com.example.ssauc.user.order.dto.OrderRequestDto;
import com.example.ssauc.user.pay.service.PaymentService;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/pay")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    // POST 요청 처리: 결제 처리 로직 수행
    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody OrderRequestDto orderRequestDto, Model model) {
        // 결제 처리 로직 실행

        // 현재 날짜와 시간 가져오기
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now1 = LocalDateTime.now();
        LocalDateTime now2 = LocalDateTime.now();
        // 원하는 형식의 포매터 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // LocalDateTime을 문자열로 포맷팅
        String formattedDateTime = now.format(formatter);
        String confirmPaymentNumber = now2.format(formatter2)+orderRequestDto.getProductId().toString();


        boolean success = paymentService.processPayment(orderRequestDto, now1, confirmPaymentNumber);

        if (success) {

            Product product = paymentService.getProductInfo(orderRequestDto.getProductId());

            // 주문 정보
            model.addAttribute("orderRequestDto", orderRequestDto);
            // 결제 시간
            model.addAttribute("timestamp", formattedDateTime);
            // 결제 번호
            model.addAttribute("confirmNumber", confirmPaymentNumber);
            // product 정보
            model.addAttribute("product", product);


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
