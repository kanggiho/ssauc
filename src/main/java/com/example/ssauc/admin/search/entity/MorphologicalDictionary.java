package com.example.ssauc.admin.search.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "morphological_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MorphologicalDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    // 형태소 분석 결과 (예: 어근, 접사 등)
    @Column(columnDefinition = "TEXT")
    private String analysis;
}
