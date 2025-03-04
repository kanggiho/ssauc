package com.example.ssauc.user.history.repository;

import com.example.ssauc.user.chat.entity.Ban;
import com.example.ssauc.user.history.dto.BanHistoryDto;
import com.example.ssauc.user.chat.repository.BanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BanHistoryService {

    private final BanRepository banRepository;

    public BanHistoryService(BanRepository banRepository) {
        this.banRepository = banRepository;
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
