package com.port.myport.controller;

import com.port.myport.domain.Board;
import com.port.myport.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    public String BoardWritePage(){
        return "boardWrite";
    }

    @PostMapping("/board/write") // 저장할 때는 보통 POST를 씁니다.
    public String boardSave(Board board) { // HTML의 name값들이 Board 객체로 쏙 들어옵니다.
        boardService.save(board); // 서비스의 save 기능을 호출!
        return "redirect:/board/list"; // 저장이 끝나면 목록 페이지로 강제 이동!
    }


}
