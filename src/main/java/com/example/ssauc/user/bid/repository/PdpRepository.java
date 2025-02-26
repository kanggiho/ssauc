package com.example.ssauc.user.bid.repository;

import com.example.ssauc.user.bid.dto.ProductInformDto;
import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PdpRepository extends JpaRepository<Product, Long> {

  @Query("SELECT new com.example.ssauc.user.bid.dto.ProductInformDto("
          + "p.name, p.tempPrice, p.createdAt, p.endAt, p.startPrice, p.price, p.imageUrl, "
          + "p.bidCount, p.dealType, p.minIncrement, u.userName, u.profileImage, "
          + "u.reputation, p.description, u.location, p.viewCount, p.likeCount)"
          + "FROM Product p "
          + "JOIN p.seller u "
          + "WHERE p.productId = :productId")
  ProductInformDto getPdpInform(@Param("productId") Long productId);


}