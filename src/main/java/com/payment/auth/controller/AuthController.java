package com.payment.auth.controller;

import com.payment.auth.dto.*;
import com.payment.auth.service.AuthService;
import com.payment.auth.user.User;
import com.payment.auth.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        AuthResponse auth = authService.login(req);

        // JWT를 HttpOnly 쿠키에 저장
        ResponseCookie cookie = ResponseCookie.from("access_token", auth.getToken())
                .httpOnly(true) // JS 접근 불가
                .secure(true)  // 배포 시 true (https 환경)
                .sameSite("None") // CSRF 방지
                .path("/")
                .maxAge(60 * 60) // 1시간
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(Authentication authentication) {
        // authentication.getName() 은 보통 email(username)을 반환함
        String email = authentication.getName();

        // DB에서 username(닉네임)을 가져오고 싶다면 UserRepository 활용
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

}
