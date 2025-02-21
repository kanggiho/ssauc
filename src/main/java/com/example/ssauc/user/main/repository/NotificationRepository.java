package com.example.ssauc.user.main.repository;

import com.example.ssauc.user.main.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}