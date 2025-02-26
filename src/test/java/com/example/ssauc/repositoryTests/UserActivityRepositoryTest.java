package com.example.ssauc.repositoryTests;

import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.mypage.entity.UserActivity;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.mypage.repository.UserActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserActivityRepositoryTest {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void testSaveUserActivity() {
        Users user = createTestUser("activityUser", "activityUser@example.com");
        Users savedUser = usersRepository.save(user);

        UserActivity activity = UserActivity.builder()
                .user(savedUser)
                .avgResponseTime(120L)
                .monthlyTradeCount(10L)
                .lastUpdated(LocalDateTime.now())
                .build();

        UserActivity savedActivity = userActivityRepository.save(activity);
        assertThat(savedActivity.getActivityId()).isNotNull();
    }

    private Users createTestUser(String name, String email) {
        return Users.builder()
                .userName(name)
                .email(email)
                .password("password")
                .phone("010-0000-0000")
                .profileImage("http://example.com/img.png")
                .reputation(4.6)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();
    }
}
