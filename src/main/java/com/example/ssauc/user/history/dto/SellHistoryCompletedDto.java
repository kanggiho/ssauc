package com.example.ssauc.user.history.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellHistoryCompletedDto {
    private Long orderId;
    private Long productId;
    private String productName;
    private String buyerName;
    private Long totalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime completedDate;
}
