package com.example.ssauc.user.product.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.product.dto.ProductInsertDto;
import com.example.ssauc.user.product.entity.Category;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.CategoryRepository;
import com.example.ssauc.user.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product insertProduct(ProductInsertDto dto, Users seller) {
        // 카테고리 검증: categoryName으로 조회
        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        // 마감 시간(LocalDateTime) 계산: auctionDate, auctionHour, auctionMinute 사용
        LocalDate closingDate = LocalDate.parse(dto.getAuctionDate());
        LocalTime closingTime = LocalTime.of(dto.getAuctionHour(), dto.getAuctionMinute());
        LocalDateTime auctionClosingDateTime = LocalDateTime.of(closingDate, closingTime);

        // Product 엔티티 빌더 사용
        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .startPrice(dto.getStartPrice())
                .imageUrl(dto.getImageUrl())
                .status("판매중")
                .createdAt(LocalDateTime.now())
                .endAt(auctionClosingDateTime)
                .viewCount(0)
                .minIncrement(dto.getMinIncrement())
                .dealType(dto.getDealType())
                .build();
        return productRepository.save(product);
    }
}
