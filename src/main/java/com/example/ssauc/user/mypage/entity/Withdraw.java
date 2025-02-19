package com.example.ssauc.user.mypage.entity;

import com.example.ssauc.user.login.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "withdraw")
public class Withdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_id")
    private Long withdrawId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Long amount;
    private Long commission;

    @Column(length = 255)
    private String bank;

    @Column(length = 255)
    private String account;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;
}
