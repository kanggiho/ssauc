package com.example.ssauc.user.list.controller;

import com.example.ssauc.user.list.Service.ListService;
import com.example.ssauc.user.list.dto.TempDto;
import jakarta.servlet.http.HttpServletRequest;
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


@Slf4j
@Controller
@RequestMapping("list")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/list")
    public String secondhandauction(Model model, @PageableDefault(size = 30) Pageable pageable, HttpSession session) {
        Page<TempDto> secondList = listService.findAll(pageable);

        model.addAttribute("secondList", secondList);
        model.addAttribute("session", session);
        return "list/list";
    }

    @GetMapping("/premiumlist")
    public String premiumlist() {
        return "list/premiumlist";
    }
}