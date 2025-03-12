package com.example.ssauc.user.chat.controller;

import com.example.ssauc.user.chat.dto.ReportRequestDto;
import com.example.ssauc.user.chat.entity.Report;
import com.example.ssauc.user.chat.service.ReportService;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final UsersRepository usersRepository;

    public ReportController(ReportService reportService, UsersRepository usersRepository) {
        this.reportService = reportService;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute ReportRequestDto dto) {

        // 1) DB에서 신고자 / 피신고자 Users 엔티티 조회
        Users reporterUser = usersRepository.findById(dto.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID: " + dto.getReporterId()));
        Users reportedUser = usersRepository.findById(dto.getReportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reportedUser ID: " + dto.getReportedUserId()));

        // 2) Report 엔티티 생성
        Report report = new Report();
        report.setReporter(reporterUser);       // ManyToOne 필드
        report.setReportedUser(reportedUser);   // ManyToOne 필드
        report.setReportReason(dto.getReason());
        report.setDetails(dto.getDetails());

        // (필요 시) 상태, 신고일자 등 기본값 설정
        report.setStatus("PENDING");
        report.setReportDate(LocalDateTime.now());

        // 신고 등록(Service호출)
        reportService.createReport(report);

        // 저장 후 페이지 이동(리다이렉트)
        return "redirect:/history/report?filter=user";
    }

}
