package com.example.ssauc.user.search.repository;


import com.example.ssauc.user.search.documnet.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    // unchanged
    List<ProductDocument> findByNameContaining(String keyword);
}