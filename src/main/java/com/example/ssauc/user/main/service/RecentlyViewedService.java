package com.example.ssauc.user.main.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.main.entity.RecentlyViewed;
import com.example.ssauc.user.main.repository.RecentlyViewedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentlyViewedService {

    private final RecentlyViewedRepository recentlyViewedRepository;

    /**
     * 특정 유저가 최근에 본 상품 목록 조회
     */
    public List<RecentlyViewed> getRecentlyViewedItems(Users user) {
        return recentlyViewedRepository.findAllByUserOrderByViewedAtDesc(user);
    }

    /**
     * 사용자가 어떤 상품을 새로 봤을 때 저장하는 메서드 예시
     */
    public void saveViewedProduct(Users user, Long productId) {
        // 이미 본 기록이 있다면(또는 중복 제거 로직을 원하는 경우) 여기서 처리 가능
        // 예를 들어, productId 중복 체크, 최근 본 기록 5개까지만 유지 등

        RecentlyViewed viewed = RecentlyViewed.builder()
                .user(user)
//                .product(/* product를 productId로 조회해서 넣어주기 */)
                .viewedAt(LocalDateTime.now())
                .build();

        recentlyViewedRepository.save(viewed);
    }
}
