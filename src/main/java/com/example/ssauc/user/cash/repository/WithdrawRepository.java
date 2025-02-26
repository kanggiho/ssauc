package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
}