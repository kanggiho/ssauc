package com.example.ssauc.user.chat.entity;

import com.example.ssauc.user.login.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ban")
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Long banId;

    // 차단한 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 차단 당한 사용자
    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false)
    private Users blockedUser;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
}
