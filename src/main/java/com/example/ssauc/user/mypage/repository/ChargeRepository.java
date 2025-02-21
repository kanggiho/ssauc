package com.example.ssauc.user.mypage.repository;

import com.example.ssauc.user.mypage.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}