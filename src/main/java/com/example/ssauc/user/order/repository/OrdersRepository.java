package com.example.ssauc.user.order.repository;

import com.example.ssauc.user.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}