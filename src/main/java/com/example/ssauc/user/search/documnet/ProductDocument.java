package com.example.ssauc.user.search.documnet;

import com.example.ssauc.user.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Data
@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/mappings/mapping_products.json")
// 또는 자동완성 기능을 추가하고 싶다면 index_mapping.json을 사용할 수 있습니다.
// @Setting(settingPath = "/elasticsearch/mappings/index_mapping.json")
public class ProductDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String productId;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String category;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String description;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    @Field(type = FieldType.Long)
    private Long viewCount;

    @Field(type = FieldType.Integer)
    private Integer dealType;

    @Field(type = FieldType.Integer)
    private Integer bidCount;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Text)
    private String suggest;

    public ProductDocument(Product product) {
        this.productId = (product.getProductId() != null)
                ? String.valueOf(product.getProductId())
                : null;

        // ✅ 카테고리 변경: getCategory() → getCategory().getName()
        this.category = (product.getCategory() != null) ? product.getCategory().getName() : null;

        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        this.endAt = product.getEndAt();
        this.viewCount = product.getViewCount();

        // ✅ dealType, bidCount, likeCount는 int → Integer 변환 필요
        this.dealType = product.getDealType();
        this.bidCount = product.getBidCount();
        this.likeCount = product.getLikeCount();
    }

    public ProductDocument() {
        // unchanged
    }
}