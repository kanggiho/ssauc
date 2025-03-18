package com.example.ssauc.user.contact.controller;

import com.example.ssauc.user.login.entity.Users;
import jakarta.servlet.http.HttpSession;
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
    public String qna(Model model, HttpSession session) {

        if(session.getAttribute("user") != null) {
            Users reportUser = (Users) session.getAttribute("user");
            String reporter = reportUser.getUserName();
            model.addAttribute("reporter", reporter);
        }

        model.addAttribute("currentPage", "qna");
        return "contact/qna";
    }

    @GetMapping("chatbot")
    public String chatbot(Model model) {
        model.addAttribute("currentPage", "chatbot");
        return "contact/chatbot";
    }
}
