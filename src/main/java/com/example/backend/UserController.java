package com.example.backend;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        userService.register(username, password);
        return "회원가입 성공";
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        return "로그인 성공!";
    }
}
