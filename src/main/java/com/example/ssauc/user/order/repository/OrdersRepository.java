package com.example.ssauc.user.order.repository;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // 세션 사용자와 seller 또는 buyer가 같은 주문을 조회 --> 결제/정산 내역에서 사용
    List<Orders> findBySellerOrBuyer(Users seller, Users buyer);
}