package com.example.ssauc.user.bid.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private Long productId;
    private Long buyerId;
    private Long sellerId;
}
