package com.example.ssauc.repositoryTests;

import com.example.ssauc.user.order.entity.Orders;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.order.repository.OrdersRepository;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.product.entity.Category;
import com.example.ssauc.user.product.entity.Product;
import com.example.ssauc.user.product.repository.CategoryRepository;
import com.example.ssauc.user.product.repository.ProductRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindOrder() {


        Users users1 = Users.builder()
                .userName("kanggiho123")
                .email("test123@example.com")  // ✅ email 값 추가
                .password("securepassword")
                .createdAt(LocalDateTime.now())
                .build();
        Users user11 = usersRepository.save(users1);


        Users users2 = Users.builder()
                .userName("kanggiho12")
                .email("test12342@example.com")  // ✅ email 값 추가
                .password("securepassword13")
                .createdAt(LocalDateTime.now())
                .build();

        Category category = new Category();
        category.setName("categoryTest");
        Category savedCategory = categoryRepository.save(category);



        Product product = new Product();
        product.setSeller(users1);
        product.setCategory(savedCategory);
        product.setName("Sample Product");
        product.setDescription("This is a sample product.");
        product.setPrice(5000L);
        product.setImageUrl("http://example.com/product.jpg");
        product.setStatus("Available");
        product.setStartPrice(1000L);
        product.setCreatedAt(LocalDateTime.now());
        product.setEndAt(LocalDateTime.now());
        product.setViewCount(0);
        Product savedProduct = productRepository.save(product);



        Users buyer = usersRepository.save(users1);
        Users seller = usersRepository.save(users2);

        Orders order = Orders.builder()
                .product(savedProduct)
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
