package com.example.ssauc.user.list.controller;

import com.example.ssauc.user.list.Service.ListService;
import com.example.ssauc.user.list.dto.ListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("list")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/list")
    public String secondhandauction() {
        return "list/list";
    }

    @GetMapping("/premiumlist")
    public String premiumlist() {
        return "list/premiumlist";
    }

    @GetMapping("/test")
    @ResponseBody
    public List<ListDto> test() {
        List<ListDto> listDto = listService.getListRepository();
        for(ListDto dto : listDto) {
            System.out.println("--------------------");
            System.out.println(dto);
            System.out.println("--------------------");
        }

        return listDto;
    }
}