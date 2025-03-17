package com.example.ssauc.user.search.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_click")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 ID는 Product 엔티티와 연관관계 설정 가능하지만, 여기서는 단순히 ID를 저장하는 방식 사용
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int clickCount = 1;  // 기본 클릭 수 1

    @UpdateTimestamp
    private LocalDateTime lastClicked;  // 마지막 클릭 시간 (자동 업데이트)
}