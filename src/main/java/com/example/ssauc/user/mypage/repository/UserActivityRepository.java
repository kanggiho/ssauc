package com.example.ssauc.user.mypage.repository;

import com.example.ssauc.user.mypage.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
}