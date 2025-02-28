package com.example.ssauc.user.bid.controller;


import com.example.ssauc.user.bid.dto.CarouselImage;
import com.example.ssauc.user.bid.dto.ProductInformDto;
import com.example.ssauc.user.bid.dto.ReportDto;
import com.example.ssauc.user.bid.service.BidService;
import com.example.ssauc.user.chat.entity.Report;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.entity.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("bid")
public class BidController {

    @Autowired
    private BidService bidService;


    @GetMapping("/bid")
    public String bidPage(@RequestParam("productId") Long productId, Model model, HttpSession session) {

        ProductInformDto dto = bidService.getBidInform(productId);
        List<CarouselImage> carouselImages = bidService.getCarouselImages(productId);


        // 표시할 정보 추가
        model.addAttribute("inform", dto);

        // 상품 정보 추가
        model.addAttribute("productId", productId);

        // 캐러셀 이미지 추가
        model.addAttribute("carouselImages", carouselImages);



        return "bid/bid"; // 해당 페이지로 이동
    }


    @GetMapping("report")
    public String report(@RequestParam("reported") Long productId, Model model) {
        Product product = bidService.getProduct(productId);
        model.addAttribute("productId", productId);
        model.addAttribute("product", product);

        return "bid/report";
    }

    @PostMapping("report")
    public ResponseEntity<String> reportPost(@RequestBody ReportDto reportDto, HttpSession session){

        Users logName = (Users) session.getAttribute("user");

        ReportDto dto = (ReportDto) reportDto;
        dto.setReporterId(logName.getUserId());

        bidService.insertReportData(dto);

        return ResponseEntity.ok("신고가 등록되었습니다.");
    }





}
