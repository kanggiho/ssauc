package com.example.ssauc.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @GetMapping
    public String index() {
        return "/admin/admin";
    }

    @GetMapping("/home")
    public String home() {
        return "/admin/adminhome";
    }
}
