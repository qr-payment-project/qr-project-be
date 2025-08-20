package com.payment.payment.product;

import com.payment.payment.dto.ResolveQrResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrService {
    private final QrLinkRepository qrRepo;
    private final ProductRepository productRepo;

    @Transactional
    public QrLink issueProductQr(Long anyUserId, Long productId, Duration ttl) {
        var p = productRepo.findById(productId).orElseThrow();

        if (!p.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "inactive product");
        }

        // 👉 발급자(sellerId)는 상품에 등록된 원래 판매자로 고정
        QrLink q = new QrLink();
        q.setProductId(productId);
        q.setSellerId(p.getSellerId());
        q.setToken(UUID.randomUUID().toString().replace("-", ""));
        q.setExpiresAt(Instant.now().plus(ttl != null ? ttl : Duration.ofMinutes(10)));

        return qrRepo.save(q);
    }

    @Transactional(readOnly=true)
    public ResolveQrResponse resolve(String token) {
        var q = qrRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid token"));
        if (q.isUsed() || q.getExpiresAt().isBefore(Instant.now()) || q.getUsedCount() >= q.getMaxUse())
            throw new ResponseStatusException(HttpStatus.GONE, "expired/used");

        var p = productRepo.findById(q.getProductId()).orElseThrow();
        if (!p.isActive()) throw new ResponseStatusException(HttpStatus.GONE, "inactive product");
        return new ResolveQrResponse(p.getId(), p.getName(), p.getPrice(), p.getSellerId(), q.getToken(), q.getExpiresAt().toString());
    }
}
