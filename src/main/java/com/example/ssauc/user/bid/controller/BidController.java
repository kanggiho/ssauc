package com.example.ssauc.user.bid.controller;


import com.example.ssauc.user.bid.dto.CarouselImage;
import com.example.ssauc.user.bid.dto.ProductInformDto;
import com.example.ssauc.user.bid.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("bid")
public class BidController {

    @Autowired
    private BidService bidService;


    @GetMapping("/bid")
    public String bidPage(@RequestParam("productId") Long productId, Model model) {

        ProductInformDto dto = bidService.getBidInform(productId);
        List<CarouselImage> carouselImages = bidService.getCarouselImages(productId);

        System.out.println("=====================================");
        System.out.println(dto);
        System.out.println("=====================================");

        // 표시할 정보 추가
        model.addAttribute("inform", dto);

        // 캐러셀 이미지 추가
        model.addAttribute("carouselImages", carouselImages);



        return "bid/bid"; // 해당 페이지로 이동
    }


    @GetMapping("report")
    public String report(Model model) {
        return "bid/report";
    }

}
