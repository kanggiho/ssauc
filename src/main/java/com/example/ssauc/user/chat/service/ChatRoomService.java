package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.ChatRoom;
import com.example.ssauc.user.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅방 생성
     */
    @Transactional
    public ChatRoom createChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .createdAt(LocalDateTime.now())
                .build();
        return chatRoomRepository.save(chatRoom);
    }



    /**
     * 채팅방 삭제
     */
    @Transactional
    public void deleteChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        chatRoomRepository.delete(chatRoom);
    }
}