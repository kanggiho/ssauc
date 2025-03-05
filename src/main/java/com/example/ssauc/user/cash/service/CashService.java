package com.example.ssauc.user.cash.service;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.ssauc.exception.PortoneVerificationException;
import java.time.LocalDateTime;

public interface CashService {

    // 환급 신청 처리 (수수료 계산 포함)
    Withdraw requestWithdraw(Users user, Long amount, String bank, String account);
    // 이번 달 환급 신청 횟수를 반환하는 메서드 추가
    int getCurrentWithdrawCount(Users user);

    // 결제 내역
    Page<CalculateDto> getPaymentCalculatesByUser(Users user, Pageable pageable);
    Page<CalculateDto> getPaymentCalculatesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    // 정산 내역
    Page<CalculateDto> getSettlementCalculatesByUser(Users user, Pageable pageable);
    Page<CalculateDto> getSettlementCalculatesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    // 충전 내역
    Page<ChargeDto> getChargesByUser(Users user, Pageable pageable);
    Page<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    // 환급 내역
    Page<WithdrawDto> getWithdrawsByUser(Users user, Pageable pageable);
    Page<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


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
