package com.port.myport.service;

import com.port.myport.domain.Board;
import com.port.myport.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository ;

    public void save(Board board) {
        boardRepository.save(board);
    }
    public List<Board> findBoards(){
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }
}
