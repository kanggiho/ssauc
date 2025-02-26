package com.example.ssauc.user.chat.entity;

import com.example.ssauc.user.login.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    // 참여 중인 채팅방
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 실제 User (FK)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 채팅 참여 시작 시각
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
