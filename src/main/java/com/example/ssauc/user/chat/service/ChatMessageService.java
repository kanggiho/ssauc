package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.ChatMessage;
import com.example.ssauc.user.chat.entity.ChatRoom;
import com.example.ssauc.user.chat.repository.ChatMessageRepository;
import com.example.ssauc.user.chat.repository.ChatRoomRepository;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    // ChatMessageRepository: 채팅 메시지 데이터에 대한 CRUD 작업을 수행하는 Repository
    private final ChatMessageRepository chatMessageRepository;

    // ChatRoomRepository: 채팅방 데이터에 대한 CRUD 작업을 수행하는 Repository
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 메시지 저장
    // 주어진 채팅방 ID, 메시지 발신자(Users 객체), 그리고 메시지 텍스트를 이용하여 새 채팅 메시지를 생성하고 저장합니다.
    public ChatMessage saveMessage(Long chatRoomId, Users senderId, String messageText) {
        // 채팅방 ID를 이용하여 채팅방 엔티티를 조회합니다.
        // 해당 채팅방이 존재하지 않으면, RuntimeException을 발생시켜 "존재하지 않는 채팅방입니다." 메시지를 전달합니다.
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        // 새로운 채팅 메시지(ChatMessage) 객체를 생성합니다.
        ChatMessage msg = new ChatMessage();

        // 조회된 채팅방 객체를 메시지의 채팅방 속성에 설정합니다.
        msg.setChatRoom(room);

        // 메시지를 보낸 사용자 정보를 메시지 객체의 발신자(sender) 속성에 설정합니다.
        msg.setSender(senderId);

        // 메시지 텍스트를 메시지 객체에 설정합니다.
        msg.setMessageText(messageText);

        // 주석: sentAt 필드는 ChatMessage 엔티티 내의 @PrePersist 어노테이션을 통해 엔티티가 저장되기 전에 자동으로 설정됩니다.

        // 채팅 메시지를 데이터베이스에 저장한 후, 저장된 메시지 객체를 반환합니다.
        return chatMessageRepository.save(msg);

    }

}





















