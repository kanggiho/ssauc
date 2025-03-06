package com.example.ssauc.user.chat.dto;

import com.example.ssauc.user.login.entity.Users;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long chatRoomId;
    private long senderId;
    private String messageText;


    // 필요 시, senderName이나 timestamp 등을 추가



}
