package com.example.ssauc.admin.search.controller;

import com.example.ssauc.admin.search.entity.MorphologicalDictionary;
import com.example.ssauc.admin.search.entity.SynonymDictionary;
import com.example.ssauc.admin.search.entity.ForbiddenDictionary;
import com.example.ssauc.admin.search.service.AdminAnalyzeService;
import com.example.ssauc.admin.search.service.SearchDictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/search")
@RequiredArgsConstructor
public class AdminSearchDictionaryController {
    private final SearchDictionaryService dictionaryService;
    private final AdminAnalyzeService analyzeService;

    // 검색어 분석기 (Elasticsearch analyze API 활용)
    @GetMapping("/analyze")
    public String analyzeText(@RequestParam String analyzer,
                              @RequestParam String text) {
        // 로그 찍어서 파라미터 확인
        System.out.println("analyzeText called with analyzer: " + analyzer + ", text: " + text);
        // 무조건 nori_analyzer만 사용
        return analyzeService.analyzeText("nori_analyzer", text);
    }

    // ▷ 형태소 사전 관련 엔드포인트
    @GetMapping("/morph")
    public List<MorphologicalDictionary> getMorphEntries() {
        return dictionaryService.getAllMorphologicalEntries();
    }

    @PostMapping("/morph")
    public MorphologicalDictionary createMorphEntry(@RequestBody MorphologicalDictionary entry) {
        return dictionaryService.addMorphologicalEntry(entry);
    }

    @PutMapping("/morph/{id}")
    public MorphologicalDictionary updateMorphEntry(@PathVariable Long id,
                                                    @RequestBody MorphologicalDictionary entry) {
        return dictionaryService.updateMorphologicalEntry(id, entry);
    }

    @DeleteMapping("/morph/{id}")
    public void deleteMorphEntry(@PathVariable Long id) {
        dictionaryService.deleteMorphologicalEntry(id);
    }

    // ▷ 유사어 사전 관련 엔드포인트
    @GetMapping("/synonym")
    public List<SynonymDictionary> getSynonymEntries() {
        return dictionaryService.getAllSynonymEntries();
    }

    @PostMapping("/synonym")
    public SynonymDictionary createSynonymEntry(@RequestBody SynonymDictionary entry) {
        return dictionaryService.addSynonymEntry(entry);
    }

    @PutMapping("/synonym/{id}")
    public SynonymDictionary updateSynonymEntry(@PathVariable Long id,
                                                @RequestBody SynonymDictionary entry) {
        return dictionaryService.updateSynonymEntry(id, entry);
    }

    @DeleteMapping("/synonym/{id}")
    public void deleteSynonymEntry(@PathVariable Long id) {
        dictionaryService.deleteSynonymEntry(id);
    }

    // ▷ 금칙어 사전 관련 엔드포인트
    @GetMapping("/forbidden")
    public List<ForbiddenDictionary> getForbiddenEntries() {
        return dictionaryService.getAllForbiddenEntries();
    }

    @PostMapping("/forbidden")
    public ForbiddenDictionary createForbiddenEntry(@RequestBody ForbiddenDictionary entry) {
        return dictionaryService.addForbiddenEntry(entry);
    }

    @PutMapping("/forbidden/{id}")
    public ForbiddenDictionary updateForbiddenEntry(@PathVariable Long id,
                                                    @RequestBody ForbiddenDictionary entry) {
        return dictionaryService.updateForbiddenEntry(id, entry);
    }

    @DeleteMapping("/forbidden/{id}")
    public void deleteForbiddenEntry(@PathVariable Long id) {
        dictionaryService.deleteForbiddenEntry(id);
    }
}
