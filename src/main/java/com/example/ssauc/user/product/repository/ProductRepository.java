package com.example.ssauc.user.product.repository;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Transactional
    @Modifying
    @Query("update Product p set p.status = '판매완료' where p.productId = :productId")
    void completeSell(@Param("productId") Long productId);

    Page<Product> findBySellerAndStatus(Users seller, String status, Pageable pageable);

    // 판매중 상태의 상품 중, 경매 마감 시간이 cutoff 이상인 항목 (판매중 리스트)
    Page<Product> findBySellerAndStatusAndEndAtGreaterThanEqual(Users seller, String status, LocalDateTime cutoff, Pageable pageable);
    // 판매중 상태의 상품 중, 경매 마감 시간이 cutoff 미만인 항목 (판매 마감 리스트)
    Page<Product> findBySellerAndStatusAndEndAtBefore(Users seller, String status, LocalDateTime cutoff, Pageable pageable);

}