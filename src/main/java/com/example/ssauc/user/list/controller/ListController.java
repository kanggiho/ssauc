package com.example.ssauc.user.list.controller;

import com.example.ssauc.user.list.Service.ListService;
import com.example.ssauc.user.list.dto.TempDto;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("list")
@Slf4j
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/list")
    public String secondhandauction(Model model, @PageableDefault(size = 30) Pageable pageable, HttpSession session) {

        if(session.getAttribute("userId") != null) {
            Page<TempDto> list = listService.list(pageable, session);
            model.addAttribute("secondList", list);
            model.addAttribute("session", session);

            return "list/list";
        }

        Page<TempDto> secondList = listService.findAll(pageable, session);
        model.addAttribute("secondList", secondList);
        model.addAttribute("session", session);

        return "list/list";
    }


    @GetMapping("/premiumlist")
    public String premiumlist() {
        return "list/premiumlist";
    }

    @GetMapping("/likelist")
    public String likelist(Model model, @PageableDefault(size = 30) Pageable pageable, HttpSession session) {
        Page<TempDto> likelist = listService.likelist(pageable, session);
        model.addAttribute("likelist", likelist);

        return "likelist/likelist";
    }

    @GetMapping("/category")
    public String category(Model model, @RequestParam("categoryId") Long categoryId, @PageableDefault(size = 30) Pageable pageable, HttpSession session) {
            Page<TempDto> categoryList = listService.categoryList(pageable, session, categoryId);
            model.addAttribute("secondList", categoryList);

            return "list/list";
    }

    @GetMapping("/price")
    public String getProductsByPrice(
            @RequestParam(name = "minPrice", required = false, defaultValue = "0") int minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "99999999") int maxPrice,
            @PageableDefault(size = 30) Pageable pageable,
            HttpSession session,
            Model model) {

        Page<TempDto> filteredProducts = listService.getProductsByPrice(pageable, session, minPrice, maxPrice);

        model.addAttribute("secondList", filteredProducts);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "list/list";
    }


    @GetMapping("/availableBid")
    public String getAvailableBid(Pageable pageable, HttpSession session, Model model) {

        if(session.getAttribute("userId") != null) {
            Page<TempDto> availableBid = listService.getAvailableBidWithLike(pageable, session);
            log.info("==============================");
            List<TempDto> list = availableBid.getContent();
            log.info("list size: withLike : " + list.size());
            log.info("list content:" + list);
            log.info("==============================");
            model.addAttribute("secondList", availableBid);
            return "list/list";
        }

        Page<TempDto> availableBid = listService.getAvailableBid(pageable);
        log.info("==============================");
        List<TempDto> list = availableBid.getContent();
        log.info("list size:" + list.size());
        log.info("list content:" + list);
        log.info("==============================");
        model.addAttribute("secondList", availableBid);
        return "list/list";
    }
}