package com.example.ssauc.user.login.entity;

import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.main.entity.ProductLike;
import com.example.ssauc.user.main.entity.RecentlyViewed;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.pay.entity.Payment;
import com.example.ssauc.user.pay.entity.Review;
import com.example.ssauc.user.product.entity.Category;
import com.example.ssauc.user.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String profileImage;

    private Double reputation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    // 연관 관계 설정

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentlyViewed> recentlyViewedProducts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductLike> likedProducts;

    @OneToMany(mappedBy = "buyer")
    private List<Orders> purchasedOrders;

    @OneToMany(mappedBy = "seller")
    private List<Orders> soldOrders;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewsGiven;

    @OneToMany(mappedBy = "reviewee")
    private List<Review> reviewsReceived;

    @OneToMany(mappedBy = "payer")
    private List<Payment> payments;

}
