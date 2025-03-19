package com.example.ssauc.user.search.service;


import com.example.ssauc.common.service.RedisService;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.search.documnet.RecentSearchDocument;
import com.example.ssauc.user.search.entity.SearchKeyword;
import com.example.ssauc.user.search.entity.UserRecentSearch;
import com.example.ssauc.user.search.repository.SearchKeywordRepository;
import com.example.ssauc.user.search.repository.UserRecentSearchRepository;
import com.example.ssauc.user.search.util.SearchLogQueryUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchKeywordRepository searchKeywordRepository;
    private final UserRecentSearchRepository userRecentSearchRepository;
    private final ElasticsearchOperations esOps;
    private final RecentSearchService recentSearchService;
    private final PopularSearchService popularSearchService;
    // RedisService를 주입받아 Redis 기반 인기 검색어 업데이트 및 조회에 사용
    private final RedisService redisService;
    private static final Logger logger = LoggerFactory.getLogger(SearchLogService.class);

    /**
     * 검색어 기록:
     * 1) 인기 검색어 DB 기록 (SearchKeyword 엔티티 업데이트)
     * 2) 사용자별 최근 검색어 DB 기록 (UserRecentSearch 엔티티 업데이트)
     * 3) ES에 최근 검색어 인덱싱 (RecentSearchDocument 인덱스 업데이트)
     * 4) Redis에 인기 검색어 업데이트 (Redis sorted set을 이용)
     */
    @Transactional
    public void recordSearch(String keyword, Users user) {
        if (user == null || keyword == null || keyword.trim().isEmpty()) return;

        // 1) 인기 검색어 DB 저장
        var sk = searchKeywordRepository.findByKeyword(keyword)
                .orElseGet(() -> new SearchKeyword(keyword, 0, LocalDateTime.now()));
        sk.setSearchCount(sk.getSearchCount() + 1);
        sk.setLastSearched(LocalDateTime.now());
        searchKeywordRepository.save(sk);
        logger.info("인기 검색어 DB 업데이트 완료: {}", sk);

        // 2) 사용자별 최근 검색어 저장
        var existing = userRecentSearchRepository.findByUserAndKeyword(user, keyword);
        if (existing.isPresent()) {
            existing.get().setSearchedAt(LocalDateTime.now());
            logger.info("기존 사용자 검색어 갱신: {}", existing.get());
        } else {
            UserRecentSearch urs = new UserRecentSearch();
            urs.setUser(user);
            urs.setKeyword(keyword);
            urs.setSearchedAt(LocalDateTime.now());
            userRecentSearchRepository.save(urs);
            logger.info("새 사용자 검색어 저장: {}", urs);
        }

        // 3) Elasticsearch 저장
        try {
            RecentSearchDocument doc = new RecentSearchDocument();
            doc.setUserId(String.valueOf(user.getUserId()));
            doc.setKeyword(keyword);
            doc.setSearchedAt(LocalDateTime.now());
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(doc).build();
            esOps.index(indexQuery, IndexCoordinates.of("recent_search"));

            System.out.println("✅ Elasticsearch 저장 완료: " + doc);
        } catch (Exception e) {
            System.err.println("❌ Elasticsearch 저장 오류: " + e.getMessage());
        }

        // 4) Redis에 인기 검색어 업데이트
        recentSearchService.addRecentSearch(String.valueOf(user.getUserId()), keyword);
        popularSearchService.addSearchKeyword(keyword);
    }

    /**
     * ES에서 최근 검색어 조회 (SearchLogQueryUtil을 활용)
     */
    public List<String> getRecentSearchesFromElasticsearch(String userId) {
        // 로그를 통해 호출 횟수 확인
        log.debug("recordSearch 호출됨 - keyword: {}, userId: {}");
        try {
            List<String> recentSearches = new SearchLogQueryUtil(esOps).findRecentSearchKeywords(userId, 20);
            System.out.println("✅ 사용자 ID: " + userId);
            System.out.println("📌 최근 검색어 목록: " + recentSearches);
            return recentSearches;
        } catch (Exception e) {
            System.err.println("❌ 최근 검색어 조회 오류: " + e.getMessage());
            return List.of(); // 오류 발생 시 빈 리스트 반환
        }
    }

    /**
     * 인기 검색어 조회:
     * 우선 Redis에서 인기 검색어 데이터를 조회하고, 없으면 DB에서 조회합니다.
     */
    public List<SearchKeyword> getPopularSearchKeywords() {
        List<SearchKeyword> popularFromRedis = redisService.getPopularSearchKeywords();
        if (popularFromRedis != null && !popularFromRedis.isEmpty()) {
            return popularFromRedis;
        }
        return searchKeywordRepository.findTop10ByOrderBySearchCountDesc();
    }

    /**
     * 사용자별 최근 검색어 삭제
     */
    @Transactional
    public void deleteUserRecentSearch(Users user, String keyword) {
        userRecentSearchRepository.findByUserAndKeyword(user, keyword)
                .ifPresent(userRecentSearchRepository::delete);
        // ES에서 삭제할 경우 별도 구현 가능 (여기서는 생략)
    }
}
