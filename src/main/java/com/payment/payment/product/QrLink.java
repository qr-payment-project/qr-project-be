package com.payment.payment.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "qr_links", uniqueConstraints = @UniqueConstraint(columnNames = "token"))
public class QrLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long sellerId;

    @Column(length = 64, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private int maxUse = 1;

    @Column(nullable = false)
    private int usedCount = 0;

    protected QrLink() {} // JPA 기본 생성자

}

