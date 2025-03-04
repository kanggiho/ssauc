package com.example.ssauc.user.product.repository;

import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Transactional
    @Modifying
    @Query("update Product p set p.status = '판매완료' where p.productId = :productId")
    void completeSell(@Param("productId") Long productId);
}