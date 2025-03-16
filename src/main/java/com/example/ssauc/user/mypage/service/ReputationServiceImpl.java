package com.example.ssauc.user.mypage.service;

import com.example.ssauc.common.service.CommonUserService;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import com.example.ssauc.user.mypage.entity.ReputationHistory;
import com.example.ssauc.user.mypage.entity.UserActivity;
import com.example.ssauc.user.mypage.repository.ReputationHistoryRepository;
import com.example.ssauc.user.mypage.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReputationServiceImpl implements ReputationService {

    private final CommonUserService commonUserService;
    private final UsersRepository userRepository;
    private final ReputationHistoryRepository reputationHistoryRepository;
    private final UserActivityRepository userActivityRepository;

    @Override
    public Users getCurrentUser(String email) {
        return commonUserService.getCurrentUser(email);
    }

    @Transactional
    public void updateReputation(Long userId, String changeType, double changeAmount) {
        Users user = getUserById(userId);

        // 기존 평판 업데이트
        double newReputation = applyReputationChange(user, changeAmount);

        // 평판 변경 이력 기록
        saveReputationHistory(user, changeType, changeAmount, newReputation);
    }

    @Transactional
    public void updateReputationForOrder(Long userId, String changeType, double changeAmount) {
        Users user = getUserById(userId);

        // 기존 평판 업데이트
        double newReputation = applyReputationChange(user, changeAmount);

        // 평판 변경 이력 기록
        saveReputationHistory(user, changeType, changeAmount, newReputation);

        // 거래 횟수 증가 및 월간 보너스 체크
        updateUserTradeCountAndBonus(user);
    }

    private Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 기존 평판 업데이트
    private double applyReputationChange(Users user, double changeAmount) {
        double newReputation = user.getReputation() + changeAmount;
        user.setReputation(newReputation);
        userRepository.save(user);
        return newReputation;
    }

    // 평판 변경 이력 기록
    private void saveReputationHistory(Users user, String changeType, double changeAmount, double newReputation) {
        ReputationHistory history = ReputationHistory.builder()
                .user(user)
                .changeType(changeType)
                .changeAmount(changeAmount)
                .newScore(newReputation)
                .createdAt(LocalDateTime.now())
                .build();
        // 거래 횟수 증가 및 월간 보너스 체크
        reputationHistoryRepository.save(history);
    }

    private void updateUserTradeCountAndBonus(Users user) {
        UserActivity activity = userActivityRepository.findByUser(user)
                .orElse(UserActivity.builder()
                        .user(user)
                        .monthlyTradeCount(0L)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        long newCount = activity.getMonthlyTradeCount() + 1;
        activity.setMonthlyTradeCount(newCount);
        activity.setLastUpdated(LocalDateTime.now());
        userActivityRepository.save(activity);

        // 월간 보너스 적용 (거래 횟수가 3이 되는 순간)
        if (newCount == 3) {
            updateReputation(user.getUserId(), "월간 거래 보너스", 3.0);
        }
    }


}
