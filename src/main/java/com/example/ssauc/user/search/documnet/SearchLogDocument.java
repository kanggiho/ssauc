package com.example.ssauc.user.search.documnet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 여러 키워드를 한 번에 검색한 로그 저장용
 * 예: ["맥북","애플워치"]
 */
@Getter
@Setter
@Document(indexName = "search_log")
public class SearchLogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    @Field(type = FieldType.Date)
    private LocalDateTime searchedAt;
}
