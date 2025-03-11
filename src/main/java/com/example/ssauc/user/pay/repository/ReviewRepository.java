package com.example.ssauc.user.pay.repository;

import com.example.ssauc.user.pay.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 로그인한 사용자가 review의 reviewee인 경우 → 수신된 리뷰
    Page<Review> findByReviewee_UserId(Long userId, Pageable pageable);

    // 로그인한 사용자가 review의 reviewer인 경우 → 작성한 리뷰
    Page<Review> findByReviewer_UserId(Long userId, Pageable pageable);
}