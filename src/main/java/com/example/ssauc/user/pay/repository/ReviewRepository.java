package com.example.ssauc.user.pay.repository;

import com.example.ssauc.user.pay.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}