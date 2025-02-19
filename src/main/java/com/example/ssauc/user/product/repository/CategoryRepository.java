package com.example.ssauc.user.product.repository;

import com.example.ssauc.user.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}