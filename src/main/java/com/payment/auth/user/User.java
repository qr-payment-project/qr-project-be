package com.payment.auth.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private long balance; // 가상머니

    @Column(nullable=false, length=100)
    private String email;

    @Column(nullable=false, length=200)
    private String password; // BCrypt 해시 저장

    @Column(nullable=false, length=30)
    private String role; // e.g. "USER", "ADMIN"
}

