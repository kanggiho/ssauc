package com.example.ssauc.user.history.service;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.chat.repository.BanRepository;
import com.example.ssauc.user.history.dto.SellHistoryCompletedDto;
import com.example.ssauc.user.history.dto.SellHistoryOngoingDto;
import com.example.ssauc.user.history.dto.SoldDetailDto;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final BanRepository banRepository;

    // 세션에서 전달된 userId를 이용하여 DB에서 최신 사용자 정보를 조회합니다.
    public Users getCurrentUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
    }

    // ===================== 차단 관리 =====================
    @Transactional(readOnly = true)
    public Page<BanHistoryDto> getBanListForUser(Long userId, Pageable pageable) {
        Page<Ban> bans = banRepository.findByUserUserId(userId, pageable);
        return bans.map(ban -> new BanHistoryDto(
                ban.getBanId(),
                ban.getBlockedUser().getUserName(), // Users 엔티티의 userName 필드 사용
                ban.getBlockedAt()
        ));
    }

    @Transactional
    public void unbanUser(Long banId, Long userId) {
        // 차단 내역 소유 여부 확인 후 삭제 처리
        Ban ban = banRepository.findById(banId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 차단 내역입니다. banId=" + banId));
        if (!ban.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("차단 해제 권한이 없습니다.");
        }
        banRepository.delete(ban);
    }

    // ===================== 판매 내역 =====================
    // 판매중 리스트
    @Transactional(readOnly = true)
    public Page<SellHistoryOngoingDto> getOngoingSellHistoryPage(Users seller, Pageable pageable) {
        // 판매중 리스트: 경매 마감 시간에 10분을 더한 값이 현재 시간보다 아직 지나지 않은 상품
        // 즉, 상품의 endAt >= (현재 시간 - 10분)인 경우만 포함
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        Page<Product> productsPage = productRepository.findBySellerAndStatusAndEndAtGreaterThanEqual(seller, "판매중", cutoff, pageable);
        return productsPage.map(p -> new SellHistoryOngoingDto(
                p.getProductId(),
                p.getName(),
                p.getTempPrice(),
                p.getPrice(),
                p.getCreatedAt(),
                p.getEndAt()
        ));
    }
    // 판매 마감 리스트
    @Transactional(readOnly = true)
    public Page<SellHistoryOngoingDto> getEndedSellHistoryPage(Users seller, Pageable pageable) {
        // 판매 마감 리스트: 판매중 상태에서 (경매 마감 시간 + 10분)이 이미 지난 상품
        // 즉, 상품의 endAt < (현재 시간 - 10분)인 경우
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        Page<Product> productsPage = productRepository.findBySellerAndStatusAndEndAtBefore(seller, "판매중", cutoff, pageable);
        return productsPage.map(p -> new SellHistoryOngoingDto(
                p.getProductId(),
                p.getName(),
                p.getTempPrice(),
                p.getPrice(),
                p.getCreatedAt(),
                p.getEndAt()
        ));
    }
    // 판매 완료 리스트
    @Transactional(readOnly = true)
    public Page<SellHistoryCompletedDto> getCompletedSellHistoryPage(Users seller, Pageable pageable) {
        Page<Product> productsPage = productRepository.findBySellerAndStatus(seller, "판매완료", pageable);
        return productsPage.map(p -> {
            // 판매 완료된 상품의 주문 정보는 보통 하나가 존재한다고 가정합니다.
            Orders order = p.getOrders().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("판매 완료 상품에 주문 정보가 없습니다."));
            return new SellHistoryCompletedDto(
                    order.getOrderId(),
                    p.getProductId(),
                    p.getName(),
                    order.getBuyer().getUserName(),
                    order.getTotalPrice(),
                    order.getOrderDate(),
                    order.getCompletedDate()
            );
        });
    }

    // 판매 완료 상세 내역
    @Transactional(readOnly = true)
    public SoldDetailDto getSoldDetailByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("해당 상품이 존재하지 않습니다."));

        Orders order = product.getOrders().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("해당 상품의 주문 정보가 없습니다."));

        return SoldDetailDto.builder()
                .productName(product.getName())
                .startPrice(product.getStartPrice())
                .createdAt(product.getCreatedAt())
                .dealType(product.getDealType())
                .imageUrl(product.getImageUrl())
                .orderId(order.getOrderId())
                .buyerName(order.getBuyer().getUserName())
                .totalPrice(order.getTotalPrice())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .postalCode(order.getPostalCode())
                .deliveryAddress(order.getDeliveryAddress())
                .orderDate(order.getOrderDate())
                .completedDate(order.getCompletedDate())
                .build();
    }
}
