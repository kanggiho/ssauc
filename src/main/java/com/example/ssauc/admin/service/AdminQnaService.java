package com.example.ssauc.admin.service;

import com.example.ssauc.admin.repository.AdminBoardRepository;
import com.example.ssauc.user.contact.entity.Board;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminQnaService
{
    @Autowired
    private AdminBoardRepository adminBoardRepository;

    @Autowired
    private UsersRepository userRepository;

    public Page<Board> getBoards(int page, String sortField, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        return adminBoardRepository.findAll(PageRequest.of(page, 10, sort));
    }

    public Board findBoardById(Long boardId) {
        return adminBoardRepository.findById(boardId).orElse(null);
    }

    public boolean updateBoardInfo(String action, long boardId) {

        Board board = adminBoardRepository.findById(boardId).orElse(null);



//        int temp = 0;

        //로직 수정
//        if(action.equals("참작")){
//            temp = 0;
//        } else if (action.equals("경고")) {
//            temp= 1;
//        } else if (action.equals("제명")) {
//            temp = 3;
//        }

        //로직 수정
//        int updateReport = adminBoardRepository.updateBoardByBoardId("완료", boardId);

        //로직 수정
//        int updateReportedUser = usersRepository.updateUserByWarningCount(temp,report.getReportedUser().getUserId());

        //로직 수정
//        return updateReport == 1 && updateReportedUser == 1;
        return true;

    }

    public List<Board> findAllBoards() {
        return adminBoardRepository.findAll();
    }
}
