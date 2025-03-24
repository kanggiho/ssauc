package com.example.ssauc.admin.search.service;

import com.example.ssauc.admin.search.entity.MorphologicalDictionary;
import com.example.ssauc.admin.search.entity.SynonymDictionary;
import com.example.ssauc.admin.search.entity.ForbiddenDictionary;
import com.example.ssauc.admin.search.repository.MorphologicalDictionaryRepository;
import com.example.ssauc.admin.search.repository.SynonymDictionaryRepository;
import com.example.ssauc.admin.search.repository.ForbiddenDictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchDictionaryService {
    private final MorphologicalDictionaryRepository morphologicalRepository;
    private final SynonymDictionaryRepository synonymRepository;
    private final ForbiddenDictionaryRepository forbiddenRepository;

    // ▷ 형태소 사전 관련
    public List<MorphologicalDictionary> getAllMorphologicalEntries() {
        return morphologicalRepository.findAll();
    }

    public MorphologicalDictionary addMorphologicalEntry(MorphologicalDictionary entry) {
        return morphologicalRepository.save(entry);
    }

    public MorphologicalDictionary updateMorphologicalEntry(Long id, MorphologicalDictionary updated) {
        Optional<MorphologicalDictionary> optional = morphologicalRepository.findById(id);
        if (optional.isPresent()) {
            MorphologicalDictionary entry = optional.get();
            entry.setWord(updated.getWord());
            entry.setAnalysis(updated.getAnalysis());
            return morphologicalRepository.save(entry);
        }
        return null;
    }

    public void deleteMorphologicalEntry(Long id) {
        morphologicalRepository.deleteById(id);
    }

    // ▷ 유사어 사전 관련
    public List<SynonymDictionary> getAllSynonymEntries() {
        return synonymRepository.findAll();
    }

    public SynonymDictionary addSynonymEntry(SynonymDictionary entry) {
        return synonymRepository.save(entry);
    }

    public SynonymDictionary updateSynonymEntry(Long id, SynonymDictionary updated) {
        Optional<SynonymDictionary> optional = synonymRepository.findById(id);
        if (optional.isPresent()) {
            SynonymDictionary entry = optional.get();
            entry.setWord(updated.getWord());
            entry.setSynonyms(updated.getSynonyms());
            return synonymRepository.save(entry);
        }
        return null;
    }

    public void deleteSynonymEntry(Long id) {
        synonymRepository.deleteById(id);
    }

    // ▷ 금칙어 사전 관련
    public List<ForbiddenDictionary> getAllForbiddenEntries() {
        return forbiddenRepository.findAll();
    }

    public ForbiddenDictionary addForbiddenEntry(ForbiddenDictionary entry) {
        return forbiddenRepository.save(entry);
    }

    public ForbiddenDictionary updateForbiddenEntry(Long id, ForbiddenDictionary updated) {
        Optional<ForbiddenDictionary> optional = forbiddenRepository.findById(id);
        if (optional.isPresent()) {
            ForbiddenDictionary entry = optional.get();
            entry.setWord(updated.getWord());
            entry.setReason(updated.getReason());
            return forbiddenRepository.save(entry);
        }
        return null;
    }

    public void deleteForbiddenEntry(Long id) {
        forbiddenRepository.deleteById(id);
    }
}
