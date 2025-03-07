package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.ChatMessage;
import com.example.ssauc.user.chat.entity.ChatRoom;
import com.example.ssauc.user.chat.repository.ChatMessageRepository;
import com.example.ssauc.user.chat.repository.ChatRoomRepository;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {


    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 메시지 저장
    public ChatMessage saveMessage(Long chatRoomId, Users senderId, String messageText) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSender(senderId);
        msg.setMessageText(messageText);

        // sentAt은 @PrePersist로 자동 설정
        return chatMessageRepository.save(msg);
    }

}





















