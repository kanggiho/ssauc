package com.example.ssauc.user.chat.entity;

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
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Users reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private Users reportedUser;

    @Column(nullable = false, length = 255)
    private String reportReason;

    @Column(length = 50)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String details;

    private LocalDateTime reportDate;
    private LocalDateTime processedAt;
}
