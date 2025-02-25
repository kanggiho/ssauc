package com.example.ssauc.user.list.Service;

import com.example.ssauc.user.list.dto.ListDto;
import com.example.ssauc.user.list.repository.ListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService {
    @Autowired
    private ListRepository listRepository;

    public List<ListDto> getListRepository() {
        return listRepository.getProductList();
    }
}
