package com.example.ssauc.user.cash.service;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.login.entity.Users;
import java.util.List;

public interface CashService {
    List<ChargeDto> getChargesByUser(Users user);
    List<WithdrawDto> getWithdrawsByUser(Users user);
    List<CalculateDto> getCalculateByUser(Users user);
}
