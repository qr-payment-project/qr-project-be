package com.payment.auth.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/me")
    public String me() { return "Hello, authenticated user!"; }
}

