package org.example.seminarbackend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://172.27.94.48", allowCredentials = "true")
@RestController
public class AuthController {


    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("jwtsecretkeyforseminar20250730xx".getBytes());

    @Autowired
    private UserRepository userRepository;

    @PostMapping({"/auth", "/auth/"})
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        System.out.println("로그인 컨트롤러 호출됨");

        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> user = userRepository.findByEmailAndPassword(email, password);

        if (user.isPresent()) {
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 20_000)) // 실제론 1시간(3600000)
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                    .compact();

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "serviceUrl", "/service"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "이메일 또는 비밀번호가 일치하지 않습니다."));
        }
    }
}
