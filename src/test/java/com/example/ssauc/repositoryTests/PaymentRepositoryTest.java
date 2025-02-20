package com.example.ssauc.repositoryTests;


import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.pay.entity.Payment;
import com.example.ssauc.user.pay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Test
    void testSaveAndFindPayment() {
        // Given - 테스트 데이터 생성
        Users payer = usersRepository.save(Users.builder()
                .userName("payerUser")
                .email("payer@example.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build());

        Orders order = ordersRepository.save(Orders.builder()
                .buyer(payer)
                .seller(payer)  // 테스트용으로 동일 사용자 설정
                .totalPrice(20000L)
                .orderDate(LocalDateTime.now())
                .build());

        Payment payment = Payment.builder()
                .order(order)
                .payer(payer)
                .amount(20000L)
                .paymentMethod("Credit Card")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        // When - 저장 및 조회
        Payment savedPayment = paymentRepository.save(payment);
        Payment foundPayment = paymentRepository.findById(savedPayment.getPaymentId()).orElse(null);

        // Then - 검증
        assertThat(foundPayment).isNotNull();
        assertThat(foundPayment.getAmount()).isEqualTo(20000L);
        assertThat(foundPayment.getPaymentMethod()).isEqualTo("Credit Card");
        assertThat(foundPayment.getPaymentStatus()).isEqualTo("Completed");
        assertThat(foundPayment.getPayer().getUserName()).isEqualTo("payerUser");
    }
}
