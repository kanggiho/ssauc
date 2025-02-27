package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findByUser(Users user);
    List<Withdraw> findByUserAndWithdrawAtBetween(Users user, LocalDateTime start, LocalDateTime end);
    // 페이징 처리 메서드 추가
    Page<Withdraw> findByUser(Users user, Pageable pageable);
    Page<Withdraw> findByUserAndWithdrawAtBetween(Users user, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
