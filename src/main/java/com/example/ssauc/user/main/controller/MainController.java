package com.example.ssauc.user.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("community")
    public String community() {
        return "community/community";
    }

    @GetMapping("cart")
    public String cart() {
        return "product/cart";
    }
}
