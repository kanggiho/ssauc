package com.example.ssauc.user.list.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("list")
public class ListController {

    @GetMapping("/list")
    public String secondhandauction() {
        return "list/list";
    }
}