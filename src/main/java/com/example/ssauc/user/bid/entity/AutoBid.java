package com.example.ssauc.user.bid.entity;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auto_bid")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    // 어떤 상품에 대한 자동입찰인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 자동입찰 하는 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // 해당 사용자가 설정한 '최대 자동 입찰 금액'
    private Long maxBidAmount;

    // 자동입찰을 등록한 시간 (우선순위가 '먼저 등록한 순'일 경우 정렬용으로 사용)
    private LocalDateTime createdAt;

    // 활성화 여부 (중간에 해지할 수 있도록)
    private boolean active;
}