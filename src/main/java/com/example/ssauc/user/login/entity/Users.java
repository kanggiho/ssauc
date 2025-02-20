package com.example.ssauc.user.login.entity;

import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.chat.entity.ChatMessage;
import com.example.ssauc.user.chat.entity.ChatParticipant;
import com.example.ssauc.user.chat.entity.Report;
import com.example.ssauc.user.contact.entity.Board;
import com.example.ssauc.user.main.entity.Notification;
import com.example.ssauc.user.main.entity.ProductLike;
import com.example.ssauc.user.main.entity.RecentlyViewed;
import com.example.ssauc.user.mypage.entity.Charge;
import com.example.ssauc.user.mypage.entity.ReputationHistory;
import com.example.ssauc.user.mypage.entity.UserActivity;
import com.example.ssauc.user.mypage.entity.Withdraw;
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

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 500)
    private String profileImage;

    private String status;
    private Double reputation;
    private int warningCount;

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





    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> purchasedOrders;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> soldOrders;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsGiven;

    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsReceived;

    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;





    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> chatParticipants;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> sentMessages;





    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsByUser;

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsAgainstUser;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ban> bansAsUser;

    @OneToMany(mappedBy = "blockedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ban> bansAsBlockedUser;





    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Withdraw> withdraws;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Charge> charges;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivity> userActivities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReputationHistory> reputationHistories;

}
