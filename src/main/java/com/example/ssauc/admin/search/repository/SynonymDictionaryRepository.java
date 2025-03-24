package com.example.ssauc.admin.search.repository;

import com.example.ssauc.admin.search.entity.SynonymDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynonymDictionaryRepository extends JpaRepository<SynonymDictionary, Long> {
    SynonymDictionary findByWord(String word);
}
