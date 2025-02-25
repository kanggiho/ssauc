package com.example.ssauc.user.product.entity;

import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.main.entity.ProductLike;
import com.example.ssauc.user.main.entity.RecentlyViewed;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // 판매자 정보
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Users seller;

    // 카테고리 정보
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long price;

    private Long startPrice;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 50)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime endAt;
    private int viewCount;

    // 거래 유형 (0: 직거래, 1: 택배, 2: 둘 다 선택)
    private int bidCount;
    private int dealType;
    private int minIncrement;
    private int likeCount;

    


    // 연관 관계 설정

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentlyViewed> recentlyViewedProducts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductLike> likedProducts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;

}
