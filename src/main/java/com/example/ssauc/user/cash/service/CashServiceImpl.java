package com.example.ssauc.user.cash.service;

import com.example.ssauc.user.cash.dto.CalculateDto;
import com.example.ssauc.user.cash.dto.ChargeDto;
import com.example.ssauc.user.cash.dto.WithdrawDto;
import com.example.ssauc.user.cash.entity.Charge;
import com.example.ssauc.user.cash.entity.Withdraw;
import com.example.ssauc.user.cash.repository.ChargeRepository;
import com.example.ssauc.user.cash.repository.WithdrawRepository;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.pay.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashServiceImpl implements CashService {

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public List<ChargeDto> getChargesByUser(Users user) {
        List<Charge> charges = chargeRepository.findByUser(user);
        return charges.stream().map(ch -> ChargeDto.builder()
                .chargeId(ch.getChargeId())
                .chargeType(ch.getChargeType())
                .amount(ch.getAmount())
                .status(ch.getStatus())
                .createdAt(ch.getCreatedAt())
                .receiptUrl(ch.getReceiptUrl())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<ChargeDto> getChargesByUser(Users user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Charge> charges = chargeRepository.findByUserAndCreatedAtBetween(user, startDate, endDate);
        return charges.stream().map(ch -> ChargeDto.builder()
                .chargeId(ch.getChargeId())
                .chargeType(ch.getChargeType())
                .amount(ch.getAmount())
                .status(ch.getStatus())
                .createdAt(ch.getCreatedAt())
                .receiptUrl(ch.getReceiptUrl())
                .build()).collect(Collectors.toList());
    }


    @Override
    public List<WithdrawDto> getWithdrawsByUser(Users user) {
        List<Withdraw> withdraws = withdrawRepository.findByUser(user);
        return withdraws.stream().map(w -> {
            String status = (w.getWithdrawAt() != null) ? "완료" : "처리중";
            long netAmount = (w.getAmount() != null && w.getCommission() != null) ? w.getAmount() - w.getCommission() : 0;
            return WithdrawDto.builder()
                    .withdrawId(w.getWithdrawId())
                    .bank(w.getBank())
                    .account(w.getAccount())
                    .netAmount(netAmount)
                    .withdrawAt(w.getWithdrawAt())
                    .requestStatus(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawDto> getWithdrawsByUser(Users user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Withdraw> withdraws = withdrawRepository.findByUserAndWithdrawAtBetween(user, startDate, endDate);
        return withdraws.stream().map(w -> {
            String status = (w.getWithdrawAt() != null) ? "완료" : "처리중";
            long netAmount = (w.getAmount() != null && w.getCommission() != null) ? w.getAmount() - w.getCommission() : 0;
            return WithdrawDto.builder()
                    .withdrawId(w.getWithdrawId())
                    .bank(w.getBank())
                    .account(w.getAccount())
                    .netAmount(netAmount)
                    .withdrawAt(w.getWithdrawAt())
                    .requestStatus(status)
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public List<CalculateDto> getCalculateByUser(Users user) {
        // session user가 seller 혹은 buyer인 주문 모두 조회 (동일 user를 두 파라미터로 전달)
        List<Orders> orders = ordersRepository.findBySellerOrBuyer(user, user);
        return orders.stream().map(order -> {
            boolean isSeller = order.getSeller().getUserId().equals(user.getUserId());
            // 주문 당 결제 내역은 order.getPayments() 리스트의 첫번째 항목을 사용 (가정)
            Payment payment = (order.getPayments() != null && !order.getPayments().isEmpty()) ? order.getPayments().get(0) : null;
            String paymentAmount = isSeller ? ("+" + order.getTotalPrice()) : ("-" + order.getTotalPrice());
            LocalDateTime paymentTime = isSeller ? order.getCompletedDate() : (payment != null ? payment.getPaymentDate() : null);
            String paymentMethod = (payment != null ? payment.getPaymentMethod() : "");
            return CalculateDto.builder()
                    .orderId(order.getOrderId())
                    .paymentAmount(paymentAmount)
                    .paymentMethod(paymentMethod)
                    .paymentTime(paymentTime)
                    .orderStatus(order.getOrderStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<CalculateDto> getCalculateByUser(Users user, LocalDateTime startDate, LocalDateTime endDate) {
        // 예시: OrdersRepository에 기간 조건을 추가한 메서드를 새로 정의한다고 가정
        List<Orders> orders = ordersRepository.findBySellerOrBuyerAndPaymentTimeBetween(user, user, startDate, endDate);
        return orders.stream().map(order -> {
            boolean isSeller = order.getSeller().getUserId().equals(user.getUserId());
            Payment payment = (order.getPayments() != null && !order.getPayments().isEmpty()) ? order.getPayments().get(0) : null;
            String paymentAmount = isSeller ? ("+" + order.getTotalPrice()) : ("-" + order.getTotalPrice());
            LocalDateTime paymentTime = isSeller ? order.getCompletedDate() : (payment != null ? payment.getPaymentDate() : null);
            String paymentMethod = (payment != null ? payment.getPaymentMethod() : "");
            return CalculateDto.builder()
                    .orderId(order.getOrderId())
                    .paymentAmount(paymentAmount)
                    .paymentMethod(paymentMethod)
                    .paymentTime(paymentTime)
                    .orderStatus(order.getOrderStatus())
                    .build();
        }).collect(Collectors.toList());
    }

}
