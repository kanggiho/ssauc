package com.example.ssauc.user.list.controller;

import com.example.ssauc.user.list.Service.ListService;
import com.example.ssauc.user.list.dto.TempDto;
import com.example.ssauc.user.list.dto.WithLikeDto;
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
}