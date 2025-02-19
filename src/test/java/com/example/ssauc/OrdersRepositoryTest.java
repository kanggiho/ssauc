package com.example.ssauc;

import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrdersRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void testSaveAndFindOrder() {


        Users users1 = Users.builder()
                .userName("kanggiho")
                .email("test@example.com")  // ✅ email 값 추가
                .password("securepassword")
                .createdAt(LocalDateTime.now())
                .build();


        Users users2 = Users.builder()
                .userName("kanggiho1")
                .email("test1@example.com")  // ✅ email 값 추가
                .password("securepassword1")
                .createdAt(LocalDateTime.now())
                .build();


        Users buyer = usersRepository.save(users1);
        Users seller = usersRepository.save(users2);

        Orders order = Orders.builder()
                .buyer(buyer)
                .seller(seller)
                .totalPrice(10000L)
                .orderDate(LocalDateTime.now())
                .build();

        Orders savedOrder = ordersRepository.save(order);
        Orders foundOrder = ordersRepository.findById(savedOrder.getOrderId()).orElse(null);

        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getTotalPrice()).isEqualTo(10000L);
    }
}
