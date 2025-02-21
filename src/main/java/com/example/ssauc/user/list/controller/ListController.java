package com.example.ssauc.user.list.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListController {

    @GetMapping("secondhandauction")
    public String secondhandauction() {
        return "list/list";
    }
}