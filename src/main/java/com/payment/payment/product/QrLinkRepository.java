package com.payment.payment.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrLinkRepository extends JpaRepository<QrLink, Long> {
    Optional<QrLink> findByToken(String token);
}
