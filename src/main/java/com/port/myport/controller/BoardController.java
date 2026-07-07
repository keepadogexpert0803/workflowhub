package com.port.myport.controller;

import com.port.myport.domain.Board;
import com.port.myport.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardservice;

    @GetMapping("/save-test")
    public String saveTest() {
        Board board = new Board();
        board.setTitle("타이틀 테스트");
        board.setContent("여긴내용부분");
        board.setAuthor("admin_yoon");

        boardservice.save(board);

        return "게시글 저장완료";

    }

}
