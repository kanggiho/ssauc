package com.example.ssauc.admin.search.repository;

import com.example.ssauc.admin.search.entity.ForbiddenDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenDictionaryRepository extends JpaRepository<ForbiddenDictionary, Long> {
    ForbiddenDictionary findByWord(String word);
}
