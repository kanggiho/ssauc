package com.example.ssauc.user.cash.service;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CashService {

    List<ChargeDto> getChargesByUser(Users user);
    List<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate);
    Page<ChargeDto> getChargesByUser(Users user, Pageable pageable);
    Page<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<WithdrawDto> getWithdrawsByUser(Users user);
    List<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate);
    Page<WithdrawDto> getWithdrawsByUser(Users user, Pageable pageable);
    Page<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<CalculateDto> getCalculateByUser(Users user);
    List<CalculateDto> getCalculateByUser(Users user, LocalDateTime startDate, LocalDateTime endDate);
    Page<CalculateDto> getCalculateByUser(Users user, Pageable pageable);
    Page<CalculateDto> getCalculateByUser(Users user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
