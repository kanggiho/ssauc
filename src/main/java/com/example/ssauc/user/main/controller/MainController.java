package com.example.ssauc.user.main.controller;

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

    @GetMapping("likelist")
    public String like() {
        return "likelist/likelist";
    }
}
