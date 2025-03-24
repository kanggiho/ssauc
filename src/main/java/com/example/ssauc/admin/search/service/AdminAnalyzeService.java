package com.example.ssauc.admin.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyzeService {
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 주어진 analyzer와 텍스트에 대해 Elasticsearch analyze API를 호출하여 분석 결과를 반환
     * analyzer는 무조건 "nori_analyzer"로 설정
     */
    public String analyzeText(String analyzer, String text) {
        try {
            var response = elasticsearchClient.indices().analyze(a -> a
                    .analyzer("nori_analyzer") // "nori_analyzer"만 사용
                    .text(text)
            );
            return response.tokens().stream()
                    .map(token -> token.token())
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "분석 실패: " + e.getMessage();
        }
    }
}
