package com.example.ssauc.admin.search.repository;

import com.example.ssauc.admin.search.entity.MorphologicalDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MorphologicalDictionaryRepository extends JpaRepository<MorphologicalDictionary, Long> {
    MorphologicalDictionary findByWord(String word);
}
