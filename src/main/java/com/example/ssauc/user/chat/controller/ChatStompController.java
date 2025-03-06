package com.example.ssauc.user.chat.controller;

import com.example.ssauc.user.chat.dto.ChatMessageDto;
import com.example.ssauc.user.chat.entity.ChatMessage;
import com.example.ssauc.user.chat.service.ChatMessageService;
import com.example.ssauc.user.login.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatStompController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(ChatMessageService chatMessageService,
                               SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    // 클라이언트 -> /app/chat/message 로 SEND 하면 여기서 처리
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageDto messageDto) {
        System.out.println("=======================");
        System.out.println(messageDto);
        System.out.println("=======================");
        Users users =  new Users();
        users.setUserId(messageDto.getSenderId());

        // 1. DB 저장
        ChatMessage saved = chatMessageService.saveMessage(
                messageDto.getChatRoomId(),
                users,
                messageDto.getMessageText()
        );

        // 2. 채팅방별로 /topic/chatroom/{id} 에 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + saved.getChatRoom().getChatRoomId(),
                convertToDto(saved)
        );
    }



//내일은 주석달자.!!!!

    private ChatMessageDto convertToDto(ChatMessage msg) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setChatRoomId(msg.getChatRoom().getChatRoomId());
        dto.setSenderId(msg.getSender().getUserId());
        dto.setMessageText(msg.getMessageText());
        return dto;
    }

}
