package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}