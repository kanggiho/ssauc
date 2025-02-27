package com.example.ssauc.user.cash.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateDto {
    private Long orderId;
    private String paymentAmount;   // "+" 또는 "-" 접두어와 total_price
    private String productName;     // orders에서 가져온 상품 이름
    private LocalDateTime paymentTime;  // 판매자: orders.completedDate, 구매자: payment.paymentDate
    private String orderStatus;     // orders.order_status
}
