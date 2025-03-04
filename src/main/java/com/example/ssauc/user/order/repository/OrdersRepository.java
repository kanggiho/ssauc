package com.example.ssauc.user.order.repository;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // 세션 사용자와 seller 또는 buyer가 같은 주문을 조회 --> 결제/정산 내역에서 사용
    Page<Orders> findBySellerOrBuyer(Users seller, Users buyer, Pageable pageable);
    // 날짜 필터 포함한 페이징
    @Query("SELECT o FROM Orders o LEFT JOIN o.payments p " +
            "WHERE (o.seller = :seller AND o.completedDate BETWEEN :start AND :end) " +
            "OR (o.buyer = :buyer AND p.paymentDate BETWEEN :start AND :end)")
    Page<Orders> findBySellerOrBuyerAndPaymentTimeBetween(@Param("seller") Users seller,
                                                          @Param("buyer") Users buyer,
                                                          @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end,
                                                          Pageable pageable);

    // 결제 내역 (구매자 기준 주문) 조회
    Page<Orders> findByBuyer(Users buyer, Pageable pageable);
    // 날짜 필터 포함한 페이징
    @Query("SELECT o FROM Orders o LEFT JOIN o.payments p " +
            "WHERE o.buyer = :buyer AND p.paymentDate BETWEEN :start AND :end")
    Page<Orders> findByBuyerAndPaymentTimeBetween(@Param("buyer") Users buyer,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  Pageable pageable);

    // 정산 내역 (판매자 기준 주문) 조회
    Page<Orders> findBySeller(Users seller, Pageable pageable);
    // 날짜 필터 포함한 페이징
    Page<Orders> findBySellerAndCompletedDateBetween(Users seller, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);



}