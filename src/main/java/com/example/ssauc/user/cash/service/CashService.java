package com.example.ssauc.user.cash.service;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.ssauc.exception.PortoneVerificationException;
import java.time.LocalDateTime;
import java.util.List;

public interface CashService {

    Page<ChargeDto> getChargesByUser(Users user, Pageable pageable);
    Page<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<WithdrawDto> getWithdrawsByUser(Users user, Pageable pageable);
    Page<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<CalculateDto> getCalculateByUser(Users user, Pageable pageable);
    Page<CalculateDto> getCalculateByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // PortOne API를 통해 결제 정보를 검증, 결제 완료 처리.
    Charge verifyAndCompletePayment(String paymentId, Long amount, Users user) throws PortoneVerificationException;

    /**
     * PortOne에서 전달받은 웹훅 요청을 처리합니다.
     *
     * @param body 웹훅 요청의 원본 본문
     * @param webhookId 웹훅 아이디
     * @param webhookTimestamp 웹훅 타임스탬프
     * @param webhookSignature 웹훅 서명
     * @throws PortoneVerificationException 검증 실패 시 예외 발생
     */
//    void handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature)
//            throws PortoneVerificationException;
}
