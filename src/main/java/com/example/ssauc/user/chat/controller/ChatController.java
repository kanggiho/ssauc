package com.example.ssauc.user.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("chat")
public class ChatController {

    @GetMapping("chat")
    public String chat(Model model) {
        Long currentUserId = 7L;

        model.addAttribute("message_id", currentUserId);


        return "chat/chat";
    }
















}
