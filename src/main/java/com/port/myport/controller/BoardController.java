package com.port.myport.controller;

import com.port.myport.domain.Board;
import com.port.myport.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/save-test")
    public String saveTest() {
        Board board = new Board();
        board.setTitle("Sample board title");
        board.setContent("Sample board content");
        board.setAuthor("admin_yoon");

        boardService.save(board);

        return "Board saved";
    }
}
