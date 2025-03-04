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
            Model model,
            HttpSession session

    ) {
        // 1) 로그인 사용자 (임시로 가정)
        Users User = (Users) session.getAttribute("user");

        // 실제로는 Spring Security 세션/Principal 등에서 가져와야 함

        // 2) DB 저장
        Board saved = boardService.createBoard(User, subject, message);

        // 3) 결과 (목록 등으로 이동)
        // 여기서는 단순히 저장 후 목록페이지로 redirect
        return "redirect:/contact/qna/list";
    }

    // [GET] 목록 보기
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("boardList", boardService.getBoardList());
        return "/contact/qna_list"; // 목록 화면 (예시)
    }

    // [GET] 상세 보기
    @GetMapping("/{boardId}")
    public String detail(@PathVariable Long boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "contact/qna_detail";
    }
}



