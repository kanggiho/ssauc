package com.example.ssauc.user.product.controller;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.dto.ProductInsertDto;
import com.example.ssauc.user.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET: 상품 등록 페이지
    @GetMapping("/insert")
    public String insertPage() {
        return "product/insert";
    }

    // POST: 상품 등록 처리 (AJAX로 호출)
    @PostMapping("/insert")
    @ResponseBody
    public ResponseEntity<String> insertProduct(@RequestBody ProductInsertDto productInsertDto, HttpSession session) {
        // 세션에서 판매자 정보 획득 (세션 키가 "user"라고 가정)
        Users seller = (Users) session.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        productService.insertProduct(productInsertDto, seller);
        return ResponseEntity.ok("상품 등록 성공!");
    }

}
