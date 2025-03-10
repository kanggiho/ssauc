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


  //ChatStompController 클래스는 STOMP 프로토콜을 통해 들어오는 채팅 메시지를 처리하는 컨트롤러
  //클라이언트에서 메시지를 전송하면 해당 메시지를 받아 DB에 저장한 후, 특정 채팅방(topic)에 메시지를 전달
@Controller
public class ChatStompController {

    // 채팅 메시지 관련 서비스 (DB 저장 등의 로직 수행)
    private final ChatMessageService chatMessageService;

    // STOMP를 통해 메시지를 전송하기 위한 템플릿
    private final SimpMessagingTemplate messagingTemplate;


    public ChatStompController(ChatMessageService chatMessageService,
                               SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

     // 클라이언트가 "/app/chat/message"로 메시지를 전송하면 호출되는 메서드입니다.
     // 이 메서드는 채팅 메시지를 받아서 DB에 저장하고, 해당 채팅방의 구독자들에게 메시지를 전송합니다.
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageDto messageDto) {
        // 전달받은 메시지 정보 확인 (디버깅용 출력)
        System.out.println("=======================");
        System.out.println(messageDto);
        System.out.println("=======================");

        // 전달받은 DTO의 senderId 값을 사용하여 Users 객체 생성
        // 실제로는 사용자 인증 정보 등을 활용하는 것이 바람직하지만, 여기서는 간단하게 객체 생성
        Users users = new Users();
        users.setUserId(messageDto.getSenderId());

        // 1. DB에 메시지 저장
        // chatMessageService의 saveMessage 메서드를 호출하여 채팅방 ID, 사용자, 메시지 텍스트를 저장하고 저장된 메시지 엔티티 반환
        ChatMessage saved = chatMessageService.saveMessage(
                messageDto.getChatRoomId(),
                users,
                messageDto.getMessageText()
        );

        // 2. 채팅방 구독자들에게 메시지 전송
        // 저장된 메시지 엔티티를 DTO로 변환하여, 채팅방 별 "/topic/chatroom/{id}" 목적지로 전송
        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + saved.getChatRoom().getChatRoomId(), // 전송할 목적지 (채팅방 ID 포함)
                convertToDto(saved)                                          // 전송할 메시지 데이터 (DTO)
        );
    }


     // ChatMessage 엔티티를 ChatMessageDto로 변환하는 메서드
     //@return 변환된 ChatMessageDto 객체
    private ChatMessageDto convertToDto(ChatMessage msg) {
        // 새로운 DTO 객체 생성
        ChatMessageDto dto = new ChatMessageDto();

        // 채팅방 ID 설정: 엔티티의 채팅방에서 채팅방 ID 추출하여 DTO에 설정
        dto.setChatRoomId(msg.getChatRoom().getChatRoomId());

        // 발신자 ID 설정: 엔티티의 사용자 정보에서 사용자 ID를 추출하여 DTO에 설정
        dto.setSenderId(msg.getSender().getUserId());

        // 메시지 텍스트 설정: 엔티티에서 메시지 텍스트 추출하여 DTO에 설정
        dto.setMessageText(msg.getMessageText());

        return dto;
    }
}
