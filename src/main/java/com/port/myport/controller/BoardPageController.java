package com.port.myport.controller;

import com.port.myport.domain.Board;
import com.port.myport.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BoardPageController {
    private final BoardService boardService;

    @GetMapping("/board/list")
    public String boardListPage(Model model) {
        model.addAttribute("boards", boardService.findBoards());
        return "boardList";
    }

    @GetMapping("/board/write")
    public String boardWritePage() {
        return "boardWrite";
    }

    @PostMapping("/board/write")
    public String boardSave(Board board) {
        boardService.save(board);
        return "redirect:/board/list";
    }
}
