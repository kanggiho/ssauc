package com.example.ssauc.user.pay.entity;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.entity.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // 리뷰를 남긴 사용자
    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Users reviewer;

    // 리뷰가 남겨진 사용자
    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Users reviewee;

    // 주문이 완료된 거래
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
