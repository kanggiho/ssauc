package com.example.ssauc.user.list.controller;

import com.example.ssauc.user.list.Service.ListService;
import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.list.dto.TempDto;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("list")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/list")
    public String secondhandauction(Model model, HttpSession session) {
        List<TempDto> list = listService.getSecondHandAuctionList();
        log.info("===============================");
        log.info(list.toString());
        log.info("===============================");
        model.addAttribute("secondList", list);

        return "/list/list";
    }

    @GetMapping("/premiumlist")
    public String premiumlist() {
        return "list/premiumlist";
    }
}