package com.example.ssauc.user.main.repository;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.main.entity.ProductLike;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByUserAndProduct(Users user, Product product);
    boolean existsByUserAndProduct(Users user, Product product);
    void deleteByUserAndProduct(Users user, Product product);
}