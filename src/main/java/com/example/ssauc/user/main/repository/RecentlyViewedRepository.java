package com.example.ssauc.user.main.repository;

import com.example.ssauc.user.main.entity.RecentlyViewed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Long> {
}