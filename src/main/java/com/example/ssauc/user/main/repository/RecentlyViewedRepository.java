package com.example.ssauc.user.main.repository;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.main.entity.RecentlyViewed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Long> {
    // 특정 유저에 대해 최근 본 상품을 날짜(시간) 내림차순으로 조회
    List<RecentlyViewed> findAllByUserOrderByViewedAtDesc(Users user);
}