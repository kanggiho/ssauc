package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByUser(Users user);
    List<Charge> findByUserAndCreatedAtBetween(Users user, LocalDateTime start, LocalDateTime end);
    // 페이징 처리 메서드 추가
    Page<Charge> findByUser(Users user, Pageable pageable);
    Page<Charge> findByUserAndCreatedAtBetween(Users user, LocalDateTime start, LocalDateTime end, Pageable pageable);
}