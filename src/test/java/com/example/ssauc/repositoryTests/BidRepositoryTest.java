package com.example.ssauc.repositoryTests;

import com.example.ssauc.user.bid.entity.Bid;
import com.example.ssauc.user.bid.repository.BidRepository;
import com.example.ssauc.user.login.entity.Users;
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
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ProductRepository productRepository; // Product 엔티티 관련 Repository

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void testSaveAndFindBid() {

        //user 테이블 생성
        Users user = new Users();
        user.setUserName("testUser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        user.setPhone("010-9876-5432");
        user.setProfileImage("https://example.com/profile.jpg");
        user.setReputation(5.0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        Users savedUser = usersRepository.save(user);

        // 카테고리(Category) 엔티티 생성 및 저장
        Category category = new Category();
        category.setName("categoryTest");
        Category savedCategory = categoryRepository.save(category);



        // 상품(Product) 엔티티 생성 및 저장
        Product product = new Product();
        product.setSeller(savedUser);
        product.setCategory(savedCategory);
        product.setName("Test Product");
        product.setDescription("This is a test product.");
        product.setPrice(10000L);
        product.setImageUrl("http://example.com/product.jpg");
        product.setStatus("Available");
        product.setCreatedAt(LocalDateTime.now());
        product.setEndAt(LocalDateTime.now());
        product.setViewCount(0);
        Product savedProduct = productRepository.save(product);



        //bid 테이블 생성
        Bid bid = new Bid();
        bid.setProduct(savedProduct);
        bid.setUser(savedUser);
        bid.setBidPrice(15000L);
        bid.setBidTime(LocalDateTime.now());
        bid.setAutoBidMax(30000L);

        // When
        Bid savedBid = bidRepository.save(bid);
        Bid foundBid = bidRepository.findById(savedBid.getBidId()).orElse(null);

        // Then
        assertThat(foundBid).isNotNull();
        assertThat(foundBid.getBidId()).isEqualTo(savedBid.getBidId());
        assertThat(foundBid.getBidPrice()).isEqualTo(15000L);
        assertThat(foundBid.getUser().getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(foundBid.getAutoBidMax()).isEqualTo(30000L);

    }
}
