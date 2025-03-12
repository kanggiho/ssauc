package com.example.ssauc.user.chat.service;

import com.example.ssauc.user.chat.entity.Report;
import com.example.ssauc.user.chat.repository.ReportRepository;
import com.example.ssauc.user.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 신고 생성
    public Report createReport(Report report) {
        // 기본값 설정
        report.setStatus("PENDING");            // 초기에 'PENDING' 상태로 설정
        report.setReportDate(LocalDateTime.now());  // 현재 시간을 신고 시간으로 설정
        // processedAt은 아직 처리되지 않았으므로 null

        return reportRepository.save(report);
    }


}
