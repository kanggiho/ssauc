package com.example.ssauc.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("signin")
    public String signin() {
        return "user/signin";
    }

    @GetMapping("signup")
    public String signup() {
        return "user/signup";
    }

    @GetMapping("mypage")
    public String mypage() {
        return "user/mypage";
    }

    @GetMapping("product")
    public String product() {
        return "product/productlist";
    }

    @GetMapping("community")
    public String community() {
        return "community/community";
    }

    @GetMapping("cart")
    public String cart() {
        return "product/cart";
    }

    @GetMapping("like")
    public String like() {
        return "product/like";
    }
}
