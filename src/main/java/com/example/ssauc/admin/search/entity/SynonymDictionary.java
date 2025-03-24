package com.example.ssauc.admin.search.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "synonym_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SynonymDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    // 유사어 목록 (쉼표 구분 문자열 혹은 JSON 배열 형태)
    @Column(columnDefinition = "TEXT")
    private String synonyms;
}
