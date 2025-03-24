package com.example.ssauc.admin.search.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forbidden_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForbiddenDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    // 금칙 사유나 설명
    @Column(columnDefinition = "TEXT")
    private String reason;
}
