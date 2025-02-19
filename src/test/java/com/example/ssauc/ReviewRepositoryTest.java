package com.example.ssauc;


import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.pay.entity.Review;
import com.example.ssauc.user.pay.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Test
    void testSaveAndFindReview() {
        // Given - 테스트 데이터 생성
        Users reviewer = usersRepository.save(Users.builder()
                .userName("reviewer")
                .email("reviewer@example.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build());

        Users reviewee = usersRepository.save(Users.builder()
                .userName("reviewee")
                .email("reviewee@example.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build());

        Orders order = ordersRepository.save(Orders.builder()
                .buyer(reviewer)
                .seller(reviewee)
                .totalPrice(5000L)
                .orderDate(LocalDateTime.now())
                .build());

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .order(order)
                .grade(5)
                .comment("Great transaction!")
                .createdAt(LocalDateTime.now())
                .build();

        // When - 저장 및 조회
        Review savedReview = reviewRepository.save(review);
        Review foundReview = reviewRepository.findById(savedReview.getReviewId()).orElse(null);

        // Then - 검증
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getGrade()).isEqualTo(5);
        assertThat(foundReview.getComment()).isEqualTo("Great transaction!");
        assertThat(foundReview.getReviewer().getUserName()).isEqualTo("reviewer");
        assertThat(foundReview.getReviewee().getUserName()).isEqualTo("reviewee");
    }
}
