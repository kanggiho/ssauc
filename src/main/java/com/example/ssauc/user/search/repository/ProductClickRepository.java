package com.example.ssauc.user.search.repository;


import com.example.ssauc.user.search.entity.ProductClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductClickRepository extends JpaRepository<ProductClick, Long> {
    Optional<ProductClick> findByProductId(Long productId);
}