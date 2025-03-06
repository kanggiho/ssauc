package com.example.ssauc.user.history.service;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.chat.repository.BanRepository;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final UsersRepository usersRepository;

    private final BanRepository banRepository;

    // 세션에서 전달된 userId를 이용하여 DB에서 최신 사용자 정보를 조회합니다.
    public Users getCurrentUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
    }

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
}
