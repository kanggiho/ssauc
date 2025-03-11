package com.example.ssauc.user.mypage.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.mypage.dto.EvaluationPendingDto;
import com.example.ssauc.user.mypage.dto.EvaluationReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MypageService {
    Users getCurrentUser(Long userId);

    // 로그인한 사용자가 수신한 리뷰 목록 조회 (reviewee가 본인)
    Page<EvaluationReviewDto> getReceivedReviews(Users user, Pageable pageable);

    // 로그인한 사용자가 작성한 리뷰 목록 조회 (reviewer가 본인)
    Page<EvaluationReviewDto> getWrittenReviews(Users user, Pageable pageable);

    // 로그인한 사용자가 아직 리뷰를 작성하지 않은 주문 목록 조회
    Page<EvaluationPendingDto> getPendingReviews(Users user, Pageable pageable);
}
