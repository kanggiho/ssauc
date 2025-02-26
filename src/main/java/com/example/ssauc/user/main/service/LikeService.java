package com.example.ssauc.user.main.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.main.entity.ProductLike;
import com.example.ssauc.user.main.repository.ProductLikeRepository;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ProductLikeRepository productLikeRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;

    public boolean toggleLike(Long userId, Long productId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (productLikeRepository.existsByUserAndProduct(user, product)) {
            // 좋아요 삭제
            productLikeRepository.deleteByUserAndProduct(user, product);
            return false;  // 좋아요 취소됨
        } else {
            // 좋아요 추가
            ProductLike like = new ProductLike(user, product);
            productLikeRepository.save(like);
            return true;  // 좋아요 눌림
        }
    }
}