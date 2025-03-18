package com.example.ssauc.user.main.repository;

import com.example.ssauc.user.main.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdAndReadStatus(Long userId, int readStatus);
}