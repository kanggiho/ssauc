package com.example.ssauc.user.bid.repository;

import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BidRepository extends JpaRepository<Bid, Long> {
    // 현재 시간보다 경매 마감 시간이 지나지 않은 것만 조회
    Page<Bid> findByUserAndProductEndAtAfter(Users user, LocalDateTime now, Pageable pageable);

}