package com.example.ssauc.user.cash.controller;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.service.CashService;
import com.example.ssauc.user.login.entity.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("cash")
public class CashController {

    @Autowired
    private CashService cashService;

    @GetMapping("/cash")
    public String cashPage(@RequestParam(value = "filter", required = false, defaultValue = "calculate") String filter,
                           @RequestParam(value = "startDate", required = false) String startDateStr,
                           @RequestParam(value = "endDate", required = false) String endDateStr,
                           HttpSession session,
                           Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("filter", filter);

        if ("charge".equals(filter)) {
            List<ChargeDto> chargeList = cashService.getChargesByUser(user);
            model.addAttribute("chargeList", chargeList);
        } else if ("withdraw".equals(filter)) {
            List<WithdrawDto> withdrawList = cashService.getWithdrawsByUser(user);
            model.addAttribute("withdrawList", withdrawList);
        } else if ("calculate".equals(filter)) {
            List<CalculateDto> calculateList = cashService.getCalculateByUser(user);
            model.addAttribute("calculateList", calculateList);
        }

        return "/cash/cash";  // templates/cash/cash.html
    }
}
