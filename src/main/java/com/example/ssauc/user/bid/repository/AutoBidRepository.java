package com.example.ssauc.user.bid.repository;

import com.example.ssauc.user.bid.entity.AutoBid;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoBidRepository extends JpaRepository<AutoBid, Long> {
    // 특정 상품에 대해 active한 자동입찰 목록을 불러오는 쿼리
    List<AutoBid> findByProductAndActiveIsTrue(Product product);

}