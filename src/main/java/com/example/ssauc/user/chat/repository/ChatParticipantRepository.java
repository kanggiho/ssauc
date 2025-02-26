package com.example.ssauc.user.chat.repository;

import com.example.ssauc.user.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    // 특정 방에 속한 참여자 목록 조회 등 메서드를 확장할 수 있음
}