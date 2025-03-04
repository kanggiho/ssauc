package com.example.ssauc.user.chat.repository;

import com.example.ssauc.user.chat.entity.Ban;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanRepository extends JpaRepository<Ban, Long> {
    // 로그인한 사용자의 차단 내역 조회 (Users 엔티티의 userId 필드를 기준) (한 페이지에 10개씩)
    Page<Ban> findByUserUserId(Long userId, Pageable pageable);
}