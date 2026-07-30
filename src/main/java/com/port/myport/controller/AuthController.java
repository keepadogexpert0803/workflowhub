package com.port.myport.controller;

import com.port.myport.domain.User;
import com.port.myport.dto.LoginRequest;
import com.port.myport.dto.UserRegisterRequest;
import com.port.myport.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("request", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        session.setAttribute("loginUserId", user.getUserId());
        session.setAttribute("loginUserRole", user.getRole().name());
        return "redirect:/tasks/admin";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/users/register")
    public String registerForm(Model model) {
        model.addAttribute("request", new UserRegisterRequest());
        return "auth/register";
    }

    @PostMapping("/users/register")
    public String register(UserRegisterRequest request) {
        userService.register(request);
        return "redirect:/login";
    }
}
