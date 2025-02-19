package com.example.ssauc.user.bid.entity;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    // 입찰한 상품
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 입찰한 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Long bidPrice; // 입찰 가격

    @Column(nullable = false)
    private LocalDateTime bidTime; // 입찰 시간

    private Long autoBidMax; // 자동 입찰 최대 금액
}
