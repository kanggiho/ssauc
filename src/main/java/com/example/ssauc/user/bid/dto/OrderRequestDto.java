package com.example.ssauc.user.bid.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private Long productId;
    private String productName;
    private Long immediatePrice;
    private String imageUrl;


}
