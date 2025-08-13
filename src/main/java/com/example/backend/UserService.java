package com.example.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    public boolean register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; // 이미 존재
        }
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        userRepository.save(user);
        return true;
    }

    // 로그인
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }
}
