package com.example.ssauc.user.list.repository;

import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.example.ssauc.user.list.dto.ListDto("
            + "p.productId, p.imageUrl, p.name, p.price, p.bidCount,"
            + "p.endAt, p.createdAt, u.location, p.likeCount) "
            + "FROM Product p "
            + "JOIN p.seller u")
    Page<ListDto> getProductList(Pageable pageable);
}