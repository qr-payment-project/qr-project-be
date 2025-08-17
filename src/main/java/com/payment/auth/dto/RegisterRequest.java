package com.payment.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min=2, max=50) String username,
        @Email @NotBlank String email,
        @Size(min=8, max=64) String password
) {}
