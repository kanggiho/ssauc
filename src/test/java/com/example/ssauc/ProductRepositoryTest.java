package com.example.ssauc;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  // JPA 관련 테스트 최적화 (내장 DB 사용 가능)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스 사용(또는 H2 등)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository; // Product 엔티티 관련 Repository

    @Autowired
    private UsersRepository usersRepository; // 판매자(Users) 엔티티 관련 Repository

    @Test
    void testSaveAndFindProduct() {
        // 판매자(Users) 엔티티 생성 및 저장
        Users seller = new Users();
        seller.setUserName("seller1");
        seller.setEmail("seller1@example.com");
        seller.setPassword("password");
        seller.setCreatedAt(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        Users savedSeller = usersRepository.save(seller);

        // Product 엔티티 생성
        Product product = new Product();
        product.setSeller(savedSeller);  // 연관관계 설정
        product.setCategoryId(1L);
        product.setName("Test Product");
        product.setDescription("This is a test product.");
        product.setPrice(10000L);
        product.setImageUrl("http://example.com/product.jpg");
        product.setStatus("Available");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setViewCount(0L);

        // Product 저장
        Product savedProduct = productRepository.save(product);

        // 저장된 상품을 ID로 조회
        Optional<Product> optionalProduct = productRepository.findById(savedProduct.getProductId());
        assertTrue(optionalProduct.isPresent(), "저장된 상품이 존재해야 합니다.");

        Product foundProduct = optionalProduct.get();
        assertEquals("Test Product", foundProduct.getName(), "상품명이 일치해야 합니다.");
        assertEquals("seller1", foundProduct.getSeller().getUserName(), "판매자명이 일치해야 합니다.");
    }
}