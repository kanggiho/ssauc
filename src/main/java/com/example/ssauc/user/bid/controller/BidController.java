package com.example.ssauc.user.bid.controller;


import com.example.ssauc.user.bid.dto.*;
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



        if(session.getAttribute("user") != null) {

            Users user = (Users) session.getAttribute("user");
            model.addAttribute("sessionId",user.getUserId());
        }else{
            model.addAttribute("sessionId","guest");
        }

        Product product = bidService.getProduct(productId);
        model.addAttribute("sellerId",product.getSeller().getUserId());

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

    // 1. 입찰 기능: 일반 입찰 요청 처리
    @PostMapping("/place")
    public ResponseEntity<?> placeBid(@RequestBody BidRequestDto bidRequestDto) {
        // 예시: 서비스에서 입찰 금액 반영, 입찰 수 증가 등의 로직 처리
        boolean success = bidService.placeBid(bidRequestDto);
        if(success){
            return ResponseEntity.ok("입찰이 성공적으로 처리되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("입찰 처리 중 오류가 발생했습니다.");
        }
    }

    // 2. 자동입찰 기능: 최대 자동 입찰 금액까지 입찰하는 로직 처리
    @PostMapping("/auto")
    public ResponseEntity<?> autoBid(@RequestBody AutoBidRequestDto autoBidRequestDto) {
        boolean success = bidService.autoBid(autoBidRequestDto);
        if(success){
            return ResponseEntity.ok("자동 입찰이 성공적으로 처리되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("자동 입찰 처리 중 오류가 발생했습니다.");
        }
    }





}
