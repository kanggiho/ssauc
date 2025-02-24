package com.example.ssauc.user.bid.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("bid")
public class BidController {


    @GetMapping("bid")
    public String bid(Model model) {
        return "bid/bid";
    }

    @GetMapping("report")
    public String report(Model model) {
        return "bid/report";
    }

}
