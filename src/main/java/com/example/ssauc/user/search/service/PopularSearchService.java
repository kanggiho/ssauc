package com.example.ssauc.user.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularSearchService {

    private final RedisTemplate<String, Object> redisTemplate;
    // Redis 키 접두어; 날짜별로 키를 분리해서 저장 (예: popular_searches:2025-03-24)
    private static final String POPULAR_SEARCH_KEY_PREFIX = "popular_searches:";

    // 날짜별 가중치 (가중치 합계는 100%로 가정)
    private static final double WEIGHT_TODAY = 0.60;
    private static final double WEIGHT_YESTERDAY = 0.25;
    private static final double WEIGHT_DAY_BEFORE = 0.15;

    /**
     * 검색어 추가: 사용자가 검색할 때 오늘 날짜의 키에 해당 검색어 점수를 1.0 증가시킵니다.
     */
    public void addSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        // 앞뒤 따옴표 제거 등 전처리
        keyword = keyword.trim().replaceAll("^\"|\"$", "");

        // 오늘 날짜에 해당하는 키 생성
        String key = POPULAR_SEARCH_KEY_PREFIX + LocalDate.now();

        // Redis 작업: StringRedisSerializer로 설정 후 점수 증가
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(key, keyword, 1.0);
        // 작업 후 JSON Serializer 복원
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    /**
     * 상위 N개의 인기 검색어 조회:
     * 오늘, 어제, 그제의 데이터를 각각 가중치(60%, 25%, 15%)를 곱해 합산한 후 내림차순 정렬합니다.
     */
    public Set<String> getTopNKeywords(int topN) {
        LocalDate today = LocalDate.now();
        String keyToday = POPULAR_SEARCH_KEY_PREFIX + today;
        String keyYesterday = POPULAR_SEARCH_KEY_PREFIX + today.minusDays(1);
        String keyDayBefore = POPULAR_SEARCH_KEY_PREFIX + today.minusDays(2);

        Map<String, Double> aggregatedScores = new HashMap<>();

        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        aggregateScoresFromKey(zSetOps, keyToday, WEIGHT_TODAY, aggregatedScores);
        aggregateScoresFromKey(zSetOps, keyYesterday, WEIGHT_YESTERDAY, aggregatedScores);
        aggregateScoresFromKey(zSetOps, keyDayBefore, WEIGHT_DAY_BEFORE, aggregatedScores);

        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 가중치 적용 후 점수가 높은 순으로 정렬하여 상위 N개 추출
        List<Map.Entry<String, Double>> sortedList = aggregatedScores.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(topN)
                .collect(Collectors.toList());

        Set<String> result = new LinkedHashSet<>();
        for (Map.Entry<String, Double> entry : sortedList) {
            result.add(entry.getKey());
        }
        return result;
    }

    /**
     * 주어진 Redis 키에서 검색어 점수를 읽어와 가중치를 적용하고, aggregatedScores 맵에 합산하는 도우미 메서드.
     */
    private void aggregateScoresFromKey(ZSetOperations<String, Object> zSetOps, String key, double weight, Map<String, Double> aggregatedScores) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = zSetOps.rangeWithScores(key, 0, -1);
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                if (tuple.getValue() != null && tuple.getScore() != null) {
                    String keyword = tuple.getValue().toString();
                    double weightedScore = tuple.getScore() * weight;
                    aggregatedScores.put(keyword, aggregatedScores.getOrDefault(keyword, 0.0) + weightedScore);
                }
            }
        }
    }

    /**
     * 오래된 데이터(예: 3일 전 이전의 인기 검색어 데이터)를 삭제하는 메서드.
     * 실제 운영에서는 Redis의 키 패턴 검색을 통해 3일 이상 지난 키를 삭제하면 됩니다.
     */
    public void cleanupOldData() {
        LocalDate cutoff = LocalDate.now().minusDays(3);
        // 예시: cutoff 이전 날짜의 키를 찾아 삭제하는 로직 구현 (RedisTemplate의 keys() 메서드 활용)
        Set<String> keys = redisTemplate.keys(POPULAR_SEARCH_KEY_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                // 키의 날짜 부분을 추출하여 cutoff 이전이면 삭제
                String datePart = key.replace(POPULAR_SEARCH_KEY_PREFIX, "");
                LocalDate keyDate = LocalDate.parse(datePart);
                if (keyDate.isBefore(cutoff)) {
                    redisTemplate.delete(key);
                }
            }
        }
    }

    /**
     * 테스트나 초기화를 위한 reset 메서드 (전체 인기 검색어 데이터 삭제)
     */
    public void reset() {
        Set<String> keys = redisTemplate.keys(POPULAR_SEARCH_KEY_PREFIX + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
