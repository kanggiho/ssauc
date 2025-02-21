package com.example.ssauc.user.chat.repository;

import com.example.ssauc.user.chat.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanRepository extends JpaRepository<Ban, Long> {
}