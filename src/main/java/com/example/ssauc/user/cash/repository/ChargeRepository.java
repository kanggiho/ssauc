package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByUser(Users user);

}