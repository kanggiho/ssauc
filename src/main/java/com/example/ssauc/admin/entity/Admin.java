package com.example.ssauc.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Table(name = "admin")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_name", length = 50, nullable = false)
    private String adminName;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    // 관계 설정
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();
}
