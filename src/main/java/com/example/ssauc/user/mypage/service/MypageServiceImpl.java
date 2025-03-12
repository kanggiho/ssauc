package com.example.ssauc.user.mypage.service;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.mypage.dto.EvaluationPendingDto;
import com.example.ssauc.user.mypage.dto.EvaluationReviewDto;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.pay.entity.Review;
import com.example.ssauc.user.pay.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UsersRepository usersRepository;
    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;

    // 세션에서 전달된 userId를 이용하여 DB에서 최신 사용자 정보를 조회합니다.
    @Override
    public Users getCurrentUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
    }

    @Override
    public Page<EvaluationReviewDto> getReceivedReviews(Users user, Pageable pageable) {
        // 로그인한 사용자가 review의 reviewee인 경우 → 수신된 리뷰 (작성자는 reviewer)
        Page<Review> reviews = reviewRepository.findByReviewee_UserId(user.getUserId(), pageable);

        return reviews.map(review -> {
            // 이미지 URL을 ',' 기준으로 분리하고 첫 번째 값만 사용
            String productImageUrl = review.getOrder().getProduct().getImageUrl();
            String mainImage = productImageUrl != null ? productImageUrl.split(",")[0] : null;

            String transactionType = getTransactionType(review, user);

            return EvaluationReviewDto.builder()
                    .reviewId(review.getReviewId())
                    .orderId(review.getOrder().getOrderId())
                    .reviewTarget(review.getReviewer().getUserName())
                    .profileImageUrl(review.getReviewer().getProfileImage())
                    .productId(review.getOrder().getProduct().getProductId())
                    .productName(review.getOrder().getProduct().getName())
                    .productImageUrl(mainImage)
                    .createdAt(review.getCreatedAt())
                    .transactionType(transactionType)
                    .build();
        });
    }

    @Override
    public Page<EvaluationReviewDto> getWrittenReviews(Users user, Pageable pageable) {
        // 로그인한 사용자가 review의 reviewer인 경우 → 작성한 리뷰 (대상은 reviewee)
        Page<Review> reviews = reviewRepository.findByReviewer_UserId(user.getUserId(), pageable);

        return reviews.map(review -> {
            // 이미지 URL을 ',' 기준으로 분리하고 첫 번째 값만 사용
            String productImageUrl = review.getOrder().getProduct().getImageUrl();
            String mainImage = productImageUrl != null ? productImageUrl.split(",")[0] : null;

            String transactionType = getTransactionType(review, user);

            return EvaluationReviewDto.builder()
                    .reviewId(review.getReviewId())
                    .orderId(review.getOrder().getOrderId())
                    .reviewTarget(review.getReviewee().getUserName())
                    .profileImageUrl(review.getReviewee().getProfileImage())
                    .productId(review.getOrder().getProduct().getProductId())
                    .productName(review.getOrder().getProduct().getName())
                    .productImageUrl(mainImage)
                    .createdAt(review.getCreatedAt())
                    .transactionType(transactionType)
                    .build();
        });
    }

    private String getTransactionType(Review review, Users user) {
        Orders order = review.getOrder();
        if (order.getBuyer().getUserId().equals(user.getUserId())) {
            return "구매";
        } else if (order.getSeller().getUserId().equals(user.getUserId())) {
            return "판매";
        }
        return "알 수 없음";
    }

    @Override
    public Page<EvaluationPendingDto> getPendingReviews(Users user, Pageable pageable) {
        // 로그인한 사용자가 아직 리뷰를 작성하지 않은 주문 조회
        Page<Orders> orders = ordersRepository.findPendingReviewOrders(user.getUserId(), pageable);
        return orders.map(order -> {
            String reviewTarget = "";
            String profileImage = "";
            String transactionType = "";

            // 주문에서 본인이 buyer면 대상은 seller, 본인이 seller면 대상은 buyer
            if (order.getBuyer().getUserId().equals(user.getUserId())) {
                reviewTarget = order.getSeller().getUserName();
                profileImage = order.getSeller().getProfileImage();
                transactionType = "구매";
            } else if (order.getSeller().getUserId().equals(user.getUserId())) {
                reviewTarget = order.getBuyer().getUserName();
                profileImage = order.getBuyer().getProfileImage();
                transactionType = "판매";
            }

            String productImageUrl = order.getProduct().getImageUrl();
            String mainImage = productImageUrl != null ? productImageUrl.split(",")[0] : null;
            return EvaluationPendingDto.builder()
                    .orderId(order.getOrderId())
                    .reviewTarget(reviewTarget)
                    .productId(order.getProduct().getProductId())
                    .productName(order.getProduct().getName())
                    .profileImageUrl(profileImage)
                    .productImageUrl(mainImage)
                    .orderDate(order.getOrderDate())
                    .transactionType(transactionType)
                    .build();
        });
    }
}
