package com.example.ssauc.user.chat.repository;

import com.example.ssauc.user.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
}