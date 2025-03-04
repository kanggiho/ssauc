package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    // 내역 확인용 메서드
    Page<Withdraw> findByUser(Users user, Pageable pageable);
    Page<Withdraw> findByUserAndWithdrawAtBetween(Users user, LocalDateTime start, LocalDateTime end, Pageable pageable);
    // 현재 월 환급 신청 건수 확인용 메서드 추가
    int countByUserAndRequestedAtBetween(Users user, LocalDateTime start, LocalDateTime end);
}
