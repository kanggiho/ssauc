package com.example.ssauc.user.contact.controller;

import com.example.ssauc.user.contact.dto.Chatbot;
import com.example.ssauc.user.contact.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatbotService chatbotService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.message")
    public Chatbot receiveUserMessage(@Payload Chatbot incoming) {
        // 사용자 메시지를 그대로 broadcast
        Chatbot userMessage = Chatbot.builder()
                .sender(incoming.getSender()) // "USER"
                .message(incoming.getMessage())
                .build();

        // ChatGPT 호출 -> 봇 응답
        Chatbot botReply = chatbotService.processUserMessage(incoming.getMessage());

        // 봇 메시지도 별도 전송 (0.5초 후 등)
        new Thread(() -> {
            try {
                Thread.sleep(100); // 0.5초 후에 응답 등 시뮬레이션
                simpMessagingTemplate.convertAndSend("/topic/chatbot", botReply);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        return userMessage;
    }
}