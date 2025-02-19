package com.example.ssauc.user.chat.repository;

import com.example.ssauc.user.chat.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}