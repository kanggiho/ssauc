package com.example.ssauc.user.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    @GetMapping("/insert")  // 상품 등록 페이지로 이동
    public String insertPage() {
        return "product/insert";
    }

}
