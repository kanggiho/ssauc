package com.example.ssauc.user.contact.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("contact")
public class ContactController {
    @GetMapping("faq")
    public String faq(Model model) {
        model.addAttribute("currentPage", "faq");
        return "contact/faq"; // faq.html을 렌더링
    }


    @GetMapping("qna")
    public String qna(Model model) {
        model.addAttribute("currentPage", "qna");
        return "contact/qna";
    }

    @GetMapping("chatbot")
    public String chatbot(Model model) {
        model.addAttribute("currentPage", "chatbot");
        return "contact/chatbot";
    }
}
