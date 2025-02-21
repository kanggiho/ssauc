package com.example.ssauc.user.mypage.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")  // "/mypage" 경로로 들어오는 요청을 처리
public class MypageController {

    @GetMapping  // GET 요청을 받아서 mypage.html을 반환
    public String mypage() {
        return "mypage/mypage"; // templates/mypage/mypage.html 파일을 렌더링
    }

    @GetMapping("/cash")  // SSAUC 머니 페이지로 이동
    public String cashPage() {
        return "mypage/cash"; // templates/mypage/cash.html 파일을 렌더링
    }

}
