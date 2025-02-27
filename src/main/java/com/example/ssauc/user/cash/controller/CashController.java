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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if(startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()){
            // 변환 예시: 시작일은 00:00, 종료일은 23:59:59로 설정
            startDate = LocalDate.parse(startDateStr).atStartOfDay();
            endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);
        }

        if ("charge".equals(filter)) {
            // 날짜 필터가 있는 경우와 없는 경우를 분기해서 처리
            List<ChargeDto> chargeList = (startDate != null && endDate != null)
                    ? cashService.getChargesByUser(user, startDate, endDate)
                    : cashService.getChargesByUser(user);
            model.addAttribute("chargeList", chargeList);
        } else if ("withdraw".equals(filter)) {
            List<WithdrawDto> withdrawList = (startDate != null && endDate != null)
                    ? cashService.getWithdrawsByUser(user, startDate, endDate)
                    : cashService.getWithdrawsByUser(user);
            model.addAttribute("withdrawList", withdrawList);
        } else if ("calculate".equals(filter)) {
            List<CalculateDto> calculateList = (startDate != null && endDate != null)
                    ? cashService.getCalculateByUser(user, startDate, endDate)
                    : cashService.getCalculateByUser(user);
            model.addAttribute("calculateList", calculateList);
        }

        return "/cash/cash";  // templates/cash/cash.html
    }
}
