package com.example.ssauc.user.product.repository;

import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}