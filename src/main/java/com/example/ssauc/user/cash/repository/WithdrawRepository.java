package com.example.ssauc.user.cash.repository;

import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findByUser(Users user);

}
