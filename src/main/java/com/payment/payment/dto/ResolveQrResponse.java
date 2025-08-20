package com.payment.payment.dto;

public record ResolveQrResponse(Long productId, String name, long price, Long sellerId, String token, String exp) {}

