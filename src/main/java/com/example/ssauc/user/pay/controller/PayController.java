package com.example.ssauc.user.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pay")
public class PayController {
    @GetMapping("pay")
    public String pay() {
        return "pay/pay";
    }
}
