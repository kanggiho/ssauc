package com.example.ssauc.user.bid.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bid")
public class BidController {


//    @GetMapping("bid")
//    public String bid(Model model) {
//        return "bid/bid";
//    }

    @GetMapping("/bid")
    public String bidPage(@RequestParam("productId") Long productId, Model model) {
        // productId로 상품 정보 조회 후 Model에 추가
//        model.addAttribute("productId", productId);
        return "bid/bid"; // 해당 페이지로 이동
    }


    @GetMapping("report")
    public String report(Model model) {
        return "bid/report";
    }

}
