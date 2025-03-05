package com.example.ssauc.user.contact.controller;

import com.example.ssauc.user.contact.entity.Board;
import com.example.ssauc.user.contact.service.BoardService;
import com.example.ssauc.user.login.entity.Users;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contact/qna")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // [POST] 문의 등록 처리
    @PostMapping("/create")
    public String createQna(
            @RequestParam String subject,
            @RequestParam String message,
            @SessionAttribute(name="user", required=false) Users user // 세션에서 가져옴
    ) {
        if (user == null) {
            // 로그인 안 된 경우 처리
            return "redirect:/login";
        }




        // 2) DB 저장
        Board saved = boardService.createBoard(user, subject, message);

        // 3) 결과 (목록 등으로 이동)
        // 여기서는 단순히 저장 후 목록페이지로 redirect
        return "redirect:/contact/qna";
    }


}



